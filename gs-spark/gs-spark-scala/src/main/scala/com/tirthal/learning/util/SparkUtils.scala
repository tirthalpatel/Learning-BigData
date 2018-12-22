package com.tirthal.learning.util

import org.apache.spark.sql.SparkSession

object SparkUtils {

  // Run Spark locally with one worker thread (i.e. no parallelism at all)
  def getSparkSession(appName: String): SparkSession = {
    SparkSession
      .builder
      .appName(appName)
      .config("spark.master", "local")  // change to "local[*]" for parallelism
      .getOrCreate
  }
}
