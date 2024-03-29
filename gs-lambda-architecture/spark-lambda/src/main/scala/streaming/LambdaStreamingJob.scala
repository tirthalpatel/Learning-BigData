package streaming

import _root_.kafka.serializer.StringDecoder
import com.datastax.spark.connector.streaming._
import com.twitter.algebird.HyperLogLogMonoid
import config.Settings
import domain.{ActivityByProduct, VisitorsByProduct}
import functions._
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import org.apache.spark.SparkContext
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import utils.SparkUtils._

import scala.util.Try

/**
  * Speed Layer for stream processing in Big Data Lambda Architecture -
  *   (i) Spark Kafka Integration with Direct Approach to consume messages of Kafka Topic
  *   (ii) Ingest Kafka Direct Stream to HDFS (store immutable master data from Kafka to HDFS for batch processing)
  *   (iii) Realtime compute of Kafka Direct Stream and store produced realtime views in Serving Layer (Cassandra)
  */
object LambdaStreamingJob {
  def main(args: Array[String]): Unit = {

    // ------------ Setup spark streaming context -------------------
    val sc = getSparkContext("Lambda with Spark")
    val sqlContext = getSQLContext(sc)
    import sqlContext.implicits._

    val batchDuration = Seconds(4)

    def streamingApp(sc: SparkContext, batchDuration: Duration) = {
      val ssc = new StreamingContext(sc, batchDuration)
      val lc = Settings.Lambda
      val topic = lc.kafkaTopic

      // -----------------------------------------------------------------------
      // ------------ Spark Kafka Integration with Direct Approach -------------
      // -----------------------------------------------------------------------
      var fromOffsets: Map[TopicAndPartition, Long] = Map.empty
      val hdfsPath = lc.hdfsPath

      Try(sqlContext.read.parquet(hdfsPath)).foreach(hdfsData =>
        fromOffsets = hdfsData.groupBy("topic", "kafkaPartition").agg(max("untilOffset").as("untilOffset"))
          .collect().map { row =>
          (TopicAndPartition(row.getAs[String]("topic"), row.getAs[Int]("kafkaPartition")), row.getAs[String]("untilOffset").toLong + 1)
        }.toMap
      )

      val kafkaDirectParams = Map(
        "metadata.broker.list" -> lc.kafkaServerConfig,
        "group.id" -> "lambda",
        "auto.offset.reset" -> "largest"
      )

      val kafkaDirectStream = fromOffsets.isEmpty match {
        case true =>
          KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
            ssc, kafkaDirectParams, Set(topic)
          )
        case false =>
          KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String, String)](
            ssc, kafkaDirectParams, fromOffsets, { mmd: MessageAndMetadata[String, String] => (mmd.key(), mmd.message()) }
          )
      }

      val activityStream = kafkaDirectStream.transform(input => {
        functions.rddToRDDActivity(input)
      }).cache()
      // -----------------------------------------------------------------------
      // ------------ Spark Kafka Integration with Direct Approach -------------
      // -----------------------------------------------------------------------

      // ----------------------------------------------------------------------------------------
      // ----------- Ingest Kafka Direct Stream to HDFS (save data from Kafka to HDFS) ----------
      // ----------------------------------------------------------------------------------------
      activityStream.foreachRDD { rdd =>
        val activityDF = rdd
          .toDF()
          .selectExpr("timestamp_hour", "referrer", "action", "prevPage", "page", "visitor", "product", "inputProps.topic as topic", "inputProps.kafkaPartition as kafkaPartition", "inputProps.fromOffset as fromOffset", "inputProps.untilOffset as untilOffset")

        activityDF
          .write
          .partitionBy("topic", "kafkaPartition", "timestamp_hour")
          .mode(SaveMode.Append)
          .parquet(hdfsPath)
      }
      // ----------------------------------------------------------------------------------------
      // ----------- Ingest Kafka Direct Stream to HDFS (save data from Kafka to HDFS) ----------
      // ----------------------------------------------------------------------------------------

      // ---------------------------------------------------------------------------------------------------------------------
      // ------ Realtime compute of Kafka Direct Stream and store produced realtime views in Serving Layer (Cassandra) -------
      // ---------------------------------------------------------------------------------------------------------------------

      // activity by product
      val activityStateSpec =
        StateSpec
          .function(mapActivityStateFunc)
          .timeout(Minutes(120))

      val statefulActivityByProduct = activityStream.transform(rdd => {
        val df = rdd.toDF()
        df.registerTempTable("activity")
        val activityByProduct = sqlContext.sql(
          """SELECT
              product,
              timestamp_hour,
              sum(case when action = 'purchase' then 1 else 0 end) as purchase_count,
              sum(case when action = 'add_to_cart' then 1 else 0 end) as add_to_cart_count,
              sum(case when action = 'page_view' then 1 else 0 end) as page_view_count
              from activity
              group by product, timestamp_hour """)
        activityByProduct
          .map { r =>
            ((r.getString(0), r.getLong(1)),
              ActivityByProduct(r.getString(0), r.getLong(1), r.getLong(2), r.getLong(3), r.getLong(4))
            )
          }
      }).mapWithState(activityStateSpec)

      val activityStateSnapshot = statefulActivityByProduct.stateSnapshots()
      activityStateSnapshot
        .reduceByKeyAndWindow(
          (_, b) => b,
          (x, _) => x,
          Seconds(30 / 4 * 4)
        ) // only save or expose the snapshot every x seconds
        .map(sr => ActivityByProduct(sr._1._1, sr._1._2, sr._2._1, sr._2._2, sr._2._3))
        .saveToCassandra("lambda", "stream_activity_by_product")

      // unique visitors by product
      val visitorStateSpec =
        StateSpec
          .function(mapVisitorsStateFunc)
          .timeout(Minutes(120))

      val statefulVisitorsByProduct = activityStream.map(a => {
        val hll = new HyperLogLogMonoid(12)
        ((a.product, a.timestamp_hour), hll(a.visitor.getBytes))
      }).mapWithState(visitorStateSpec)

      val visitorStateSnapshot = statefulVisitorsByProduct.stateSnapshots()
      visitorStateSnapshot
        .reduceByKeyAndWindow(
          (_, b) => b,
          (x, _) => x,
          Seconds(30 / 4 * 4)
        ) // only save or expose the snapshot every x seconds
        .map(sr => VisitorsByProduct(sr._1._1, sr._1._2, sr._2.approximateSize.estimate))
        .saveToCassandra("lambda", "stream_visitors_by_product")

      // ---------------------------------------------------------------------------------------------------------------------
      // ------ Realtime compute of Kafka Direct Stream and store produced realtime views in Serving Layer (Cassandra) -------
      // ---------------------------------------------------------------------------------------------------------------------

      ssc
    }

    val ssc = getStreamingContext(streamingApp, sc, batchDuration)
    //ssc.remember(Minutes(5))
    ssc.start()
    ssc.awaitTermination()
  }
}
