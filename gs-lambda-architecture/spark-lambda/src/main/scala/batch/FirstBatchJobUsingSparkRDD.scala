package batch

import config.Settings
import domain._
import utils.SparkUtils.{getSparkContext, isIDE}

/**
  * First Batch Job of Aggregating with Spark RDD API
  */
object FirstBatchJobUsingSparkRDD {
  def main (args: Array[String]): Unit = {

    // ----------- access BatchJob config ----------------
    val sbjc = Settings.SparkBatchJob

    // ------------ setup spark context -------------------
    val sc = getSparkContext("Lambda with Spark")

    // ------------ initialize input RDD -------------------
    val sourceFile = isIDE match {
      case true => sbjc.sourceFileForIde
      case false => sbjc.sourceFileForVagrant
    }
    val input = sc.textFile(sourceFile)

    println("************************************ Input Activity RDD:")
    val inputRDD = input.flatMap{ line =>
      // used flatMap, b'cas it can return by unboxing Some (return Activity domain) or None (return nothing)
      val record = line.split("\\t")
      val MS_IN_HOUR = 1000 * 60 * 60
      if( record.length == 7)
        Some(Activity(record(0).toLong / MS_IN_HOUR * MS_IN_HOUR, record(1), record(2), record(3), record(4), record(5), record(6)))
      else
        None
    }

    inputRDD.foreach(println)  // spark action results in Job execution (see in SparkUI)
    Thread sleep 1000*30 // Hold current thread for 30 seconds

    // ------------- perform aggregation ------------------

    // (i) prepare RDD of "per product per hour" key and "Activity domain" value
    println("************************************ Keyed By Product RDD:")
    val keyedByProduct = inputRDD.keyBy(a => (a.product, a.timestamp_hour)).cache()

    keyedByProduct.foreach(println)
    Thread sleep 1000*30 // Hold current thread for 30 seconds

    // (ii) count unique number of visits per product per hour
    println("************************************ Visitors By Product Aggregation:")
    val visitorsByProduct = keyedByProduct.mapValues(a => a.visitor)
                                          .distinct()
                                          .countByKey()

    visitorsByProduct.foreach(println)
    Thread sleep 1000*30 // Hold current thread for 30 seconds

    // (iii) count different activities per product per hour
    println("************************************ Activity By Product Aggregation:")
    val activityByProduct = keyedByProduct.mapValues{ a =>
      a.action match {
        case "purchase" => (1, 0, 0)
        case "add_to_cart" => (0, 1, 0)
        case "page_view" => (0, 0, 1)
      }
    }.reduceByKey{ (a, b) => (a._1 + b._1, a._2 + b._2, a._3 + b._3) }

    activityByProduct.foreach(println)
    Thread sleep 1000*30 // Hold current thread for 30 seconds
  }
}
