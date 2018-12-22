package com.tirthal.learning.example

import java.util.regex.Pattern

import com.tirthal.learning.util.SparkUtils
import org.apache.commons.csv.{CSVFormat, CSVPrinter}

object WordCountWithSparkRdd {

  private val SPACE = Pattern.compile(" ")

  def main(args : Array[String]) {

    if (args.length < 1) {
      System.err.println("Missing input file path!!! Usage: WordCountWithSparkRdd <file>")
      System.exit(1)
    }

    // No need to create SparkContext from Spark 2.0
    // Create a SparkSession - a unified entry point for manipulating data with Spark
    val spark = SparkUtils.getSparkSession("Word Count with Spark and Scala")

    // -------
    // Create a Spark RDD using the input file
    // -------
    val lines = spark.read.textFile(args(0)).rdd

    // -------
    // Do transformations
    // -------

    // Each line is split into words using flatMap transformation
    val words = lines.flatMap(_.split(SPACE.pattern()))

    // Assign the value ‘1’ to each of the work-keys using mapToPairs transformation
    val ones = words.map(word => (word, 1))

    // Add values of similar keys to get the final word count using reduceByKey transformation
    val wordCounts = ones.reduceByKey(_ + _)

    // Cache RDD
    wordCounts.cache()

    // -------
    // Perform actions
    // -------

    // Find total number of words
    val totalWords: Long = wordCounts.count
    System.out.println("*** Total words = " + totalWords)

    // Return all elements of RDD using collect action and print
    System.out.println("*** RDD collect action:")
    val output = wordCounts.collect()
    for (tuple <- output) {
      System.out.println(tuple._1 + ": " + tuple._2)
    }

    // Print word count result using foreach action on RDD
    System.out.println("*** RDD foreach action:")
    wordCounts.foreach(tuple => System.out.println(tuple._1 + ": " + tuple._2))

    // Convert this RDD into CSV
    System.out.println("*** RDD to CSV conversion and print:")
    val printer = new CSVPrinter(Console.out, CSVFormat.DEFAULT)
    printer.printRecords(wordCounts.collect)

    // -------
    // Done
    // -------
    spark.close
  }
}
