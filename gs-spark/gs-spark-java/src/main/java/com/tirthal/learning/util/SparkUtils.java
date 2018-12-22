package com.tirthal.learning.util;

import org.apache.spark.sql.SparkSession;

public class SparkUtils {

    public static SparkSession getSparkSession(String appName) {

        // Run Spark locally with one worker thread (i.e. no parallelism at all)
        return SparkSession
                .builder()
                .appName(appName)
                .config("spark.master", "local") // change to "local[*]" for parallelism
                .getOrCreate();
    }
}
