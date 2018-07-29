package batch

import config.Settings
import utils.SparkUtils._

/**
  * Batch Layer for batch processing in Big Data Lambda Architecture -
  *   (i) Batch compute from HDFS (distributed storage of immutable master data)
  *   (ii) Store produced batch views in Serving Layer (Cassandra)
  */
object LambdaBatchJob {
  def main (args: Array[String]): Unit = {

    // ------------ Setup spark context -------------------
    val sc = getSparkContext("Lambda with Spark")
    val sqlContext = getSQLContext(sc)
    val lc = Settings.Lambda

    // ----------------------------------------------------------------------------------------
    // ------ Batch compute from HDFS & Store produced batch views in Serving Layer -----------
    // ----------------------------------------------------------------------------------------
    val inputDF = sqlContext.read.parquet(lc.hdfsPath)
      .where("unix_timestamp() - timestamp_hour / 1000 <= 60 * 60 * 6")

    inputDF.registerTempTable("activity")
    val visitorsByProduct = sqlContext.sql(
      """SELECT product, timestamp_hour, COUNT(DISTINCT visitor) as unique_visitors
        |FROM activity GROUP BY product, timestamp_hour
      """.stripMargin)

    visitorsByProduct
      .write
      .format("org.apache.spark.sql.cassandra")
      .options(Map( "keyspace" -> "lambda", "table" -> "batch_visitors_by_product"))
      .save()

    val activityByProduct = sqlContext.sql("""SELECT
                                            product,
                                            timestamp_hour,
                                            sum(case when action = 'purchase' then 1 else 0 end) as purchase_count,
                                            sum(case when action = 'add_to_cart' then 1 else 0 end) as add_to_cart_count,
                                            sum(case when action = 'page_view' then 1 else 0 end) as page_view_count
                                            from activity
                                            group by product, timestamp_hour """).cache()

    activityByProduct
      .write
      .format("org.apache.spark.sql.cassandra")
      .options(Map( "keyspace" -> "lambda", "table" -> "batch_activity_by_product"))
      .save()
    // ----------------------------------------------------------------------------------------
    // ------ Batch compute from HDFS & Store produced batch views in Serving Layer -----------
    // ----------------------------------------------------------------------------------------
  }
}