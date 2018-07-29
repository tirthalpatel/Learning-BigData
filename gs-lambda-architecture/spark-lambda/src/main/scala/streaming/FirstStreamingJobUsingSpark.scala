package streaming

import com.twitter.algebird.HyperLogLogMonoid
import config.Settings
import functions._
import domain.{Activity, ActivityByProduct, VisitorsByProduct}
import org.apache.spark.SparkContext
import org.apache.spark.streaming._
import utils.SparkUtils._

/**
  * First Spark Streaming Job of Aggregating Micro Batch (i.e. Spark splits the stream into micro batches)
  *
  * Concepts:
  *   - DStream is a continuous stream of RDD (Spark abstraction)
  *   - Stream Stateless transform operation allows any arbitrary RDD-to-RDD function to act on the DStream
  *   - Stream Stateful transform operation uses data or intermediate results from previous batches and computes the result of the current batch
  */
object FirstStreamingJobUsingSpark {
  def main(args: Array[String]): Unit = {

    // ----------- Access StreamingJob config ----------------
    val ssjc = Settings.SparkStreamingJob

    // ------------ Setup spark streaming context -------------------
    val sc = getSparkContext("Lambda with Spark")
    val sqlContext = getSQLContext(sc)
    import sqlContext.implicits._

    val batchDuration = Seconds(4)

    def streamingApp(sc: SparkContext, batchDuration: Duration) = {
      val ssc = new StreamingContext(sc, batchDuration)

      // ------------ Initialize DStream -------------------
      val inputPath = isIDE match {
        case true => ssjc.sourceFileForIde
        case false => ssjc.sourceFileForVagrant
      }

      // Spark streaming to receive text file inputs from file system directory and return DStream
      val textDStream = ssc.textFileStream(inputPath)
      if (isIDE) {
        textDStream.print
      }

      // Stateless Transformation - Text Line RDD into Activity RDD on the DStream
      val activityStream = textDStream.transform(input => {
        input.flatMap { line =>
          val record = line.split("\\t")
          val MS_IN_HOUR = 1000 * 60 * 60
          if (record.length == 7)
            Some(Activity(record(0).toLong / MS_IN_HOUR * MS_IN_HOUR, record(1), record(2), record(3), record(4), record(5), record(6)))
          else
            None
        }
      }).cache()
      if (isIDE) {
        activityStream.print
      }

      // --------------------- Perform aggregation -------------------

      // --------------------------------------------------------
      // (i) count different activities per product per hour
      // --------------------------------------------------------

      // Stateless Transformation - Activity RDD into ActivityByProduct RDD on the DStream
      val activityByProductStream = activityStream.transform(rdd => {
        val df = rdd.toDF()
        df.registerTempTable("activity")
        val activityByProduct = sqlContext.sql("""SELECT
                                            product,
                                            timestamp_hour,
                                            sum(case when action = 'purchase' then 1 else 0 end) as purchase_count,
                                            sum(case when action = 'add_to_cart' then 1 else 0 end) as add_to_cart_count,
                                            sum(case when action = 'page_view' then 1 else 0 end) as page_view_count
                                            from activity
                                            group by product, timestamp_hour """)
        activityByProduct
          .map { r => ((r.getString(0), r.getLong(1)),
            ActivityByProduct(r.getString(0), r.getLong(1), r.getLong(2), r.getLong(3), r.getLong(4))
            ) }
      }).cache()
      if (isIDE) {
        activityByProductStream.print
      }

      // Stateful stream processing using mapWithState (mapWithState can provide up to higher performance when compared to updateStateByKey alternative)
      val activityStateSpec =
        StateSpec
          .function(mapActivityStateFunc)   // state update function
          .timeout(Minutes(120))            // duration after which the state of an idle key will be removed

      val statefulActivityByProduct = activityByProductStream.mapWithState(activityStateSpec)

      val activityStateSnapshot = statefulActivityByProduct.stateSnapshots()
      activityStateSnapshot
        .reduceByKeyAndWindow(
          (_, b) => b,
          (x, _) => x,
          Seconds(30 / 4 * 4),
          filterFunc = (_) => false
        ) // only save or expose the snapshot every x seconds
        .foreachRDD(rdd => rdd.map(sr => ActivityByProduct(sr._1._1, sr._1._2, sr._2._1, sr._2._2, sr._2._3))
          .toDF().registerTempTable("ActivityByProduct"))
      if (isIDE) {
        activityStateSnapshot.print
      }

      // --------------------------------------------------------
      // (ii) unique visitors by product
      // --------------------------------------------------------

      // Stateful stream processing using mapWithState and HyperLogLog
      val visitorStateSpec =
        StateSpec
          .function(mapVisitorsStateFunc)
          .timeout(Minutes(120))

      val hll = new HyperLogLogMonoid(12)
      val statefulVisitorsByProduct = activityStream.map( a => {
        ((a.product, a.timestamp_hour), hll(a.visitor.getBytes))
      } ).mapWithState(visitorStateSpec)

      val visitorStateSnapshot = statefulVisitorsByProduct.stateSnapshots()
      visitorStateSnapshot
        .reduceByKeyAndWindow(
          (_, b) => b,
          (x, _) => x,
          Seconds(30 / 4 * 4),
          filterFunc = (_) => false
        ) // only save or expose the snapshot every x seconds
        .foreachRDD(rdd => rdd.map(sr => VisitorsByProduct(sr._1._1, sr._1._2, sr._2.approximateSize.estimate))
        .toDF().registerTempTable("VisitorsByProduct"))
      if (isIDE) {
        visitorStateSnapshot.print
      }

      ssc
    }

    // Kick off spark streaming job
    val ssc = getStreamingContext(streamingApp, sc, batchDuration)
    ssc.start()               // Start the computation
    ssc.awaitTermination()    // Wait for termination
  }
}
