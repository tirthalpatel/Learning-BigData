package batch

import config.Settings
import org.apache.spark.sql.SaveMode
import domain._
import utils.SparkUtils._

/**
  * First Batch Job of Aggregating with Spark DataFrame API
  */
object FirstBatchJobUsingSparkDF {

  // isIDE = true refers to the code to be executed when running the program using Intellij IDE (see SparkUtils.scala)

  def main (args: Array[String]): Unit = {

    // --------------- BatchJob config -----------
    val sbjc = Settings.SparkBatchJob

    // --------------- setup spark sql context -----------------
    val sc = getSparkContext("Lambda with Spark")
    val sqlContext = getSQLContext(sc)

    import org.apache.spark.sql.functions._
    import sqlContext.implicits._

    // ---------------- initialize input DataFrame -------------
    val sourceFile = isIDE match {
      case true => sbjc.sourceFileForIde
      case false => sbjc.sourceFileForVagrant
    }

    val input = sc.textFile(sourceFile)

    println("************************************ Input Activity DataFrame:")
    val inputDF = input.flatMap{ line =>
      val record = line.split("\\t")
      val MS_IN_HOUR = 1000 * 60 * 60
      if (record.length == 7)
        Some(Activity(record(0).toLong / MS_IN_HOUR * MS_IN_HOUR, record(1), record(2), record(3), record(4), record(5), record(6)))
      else
        None
    }.toDF() // toDF() - converts RDD to DataFrame

    if (isIDE) {
      inputDF.foreach(println) // spark action results in Job execution (see in SparkUI)
      Thread sleep 1000 * 30 // Hold current thread for 30 seconds
    }

    // --------------------- perform aggregation -------------------

    // (i) prepare "activity" table containing activity records of product per hour"
    println("************************************ Select DataFrame:")
    val df = inputDF.select(
      add_months(from_unixtime(inputDF("timestamp_hour") / 1000), 1).as("timestamp_hour"),
      inputDF("referrer"), inputDF("action"), inputDF("prevPage"), inputDF("page"), inputDF("visitor"), inputDF("product")
    ).cache()

    df.foreach(println)
    if (isIDE) Thread sleep 1000*30 // Hold current thread for 30 seconds

    df.registerTempTable("activity")

    // (ii) count unique number of visits per product per hour
    println("************************************ Visitors By Product Aggregation:")
    val visitorsByProduct = sqlContext.sql(
      """SELECT product, timestamp_hour, COUNT(DISTINCT visitor) as unique_visitors
        |FROM activity GROUP BY product, timestamp_hour
      """.stripMargin)
    if (isIDE) visitorsByProduct.printSchema()

    visitorsByProduct.foreach(println)
    if (isIDE) Thread sleep 1000*30 // Hold current thread for 30 seconds

    // (iii) count different activities per product per hour
    println("************************************ Activity By Product Aggregation:")
    val activityByProduct = sqlContext.sql("""SELECT
                                              product,
                                              timestamp_hour,
                                              sum(case when action = 'purchase' then 1 else 0 end) as purchase_count,
                                              sum(case when action = 'add_to_cart' then 1 else 0 end) as add_to_cart_count,
                                              sum(case when action = 'page_view' then 1 else 0 end) as page_view_count
                                              from activity
                                              group by product, timestamp_hour
                                           """).cache()
    if (isIDE) activityByProduct.printSchema()

    activityByProduct.foreach(println)
    if (isIDE) Thread sleep 1000*30 // Hold current thread for 30 seconds

    // ----------------- create user defined function (UDF) -------------------------

    // (iv) under exposed products count per product per hour
    if (isIDE) {
      println("************************************ Under Exposed Products UDF:")
      sqlContext.udf.register("UnderExposed", (pageViewCount: Long, purchaseCount: Long) => if (purchaseCount == 0) 0 else pageViewCount / purchaseCount)

      activityByProduct.registerTempTable("activityByProduct")

      val underExposedProducts = sqlContext.sql("""SELECT
                                                 product,
                                                 timestamp_hour,
                                                 UnderExposed(page_view_count, purchase_count) as negative_exposure
                                                 from activityByProduct
                                                 order by negative_exposure DESC
                                                 limit 5
                                              """).cache()
      underExposedProducts.printSchema()

      underExposedProducts.foreach(println)
      Thread sleep 1000 * 30 // Hold current thread for 30 seconds
    }

    // ---------------- save result to HDFS

    // Execute when running the program when not IDE (i.e. running on Cluster deploy mode via Spark-Submit)
    if (!isIDE) {
      // Apache Parquet is a columnar storage format available to any project in the Hadoop ecosystem, regardless of the choice of data processing framework, data model or programming language
      activityByProduct.write.partitionBy("timestamp_hour").mode(SaveMode.Append).parquet("hdfs://lambda-pluralsight:9000/lambda/batch1")
    }
  }
}
