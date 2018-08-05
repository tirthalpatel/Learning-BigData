package examples;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import utils.SparkUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class WordCountSparkJavaExample {

    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: JavaWordCount <file>");
            System.exit(1);
        }

        // No need to create SparkContext from Spark 2.0
        // Create a SparkSession - a unified entry point for manipulating data with Spark
        SparkSession spark = SparkUtils.getSparkSession("JavaWordCount");

        // -------
        // Create a Spark RDD using the input file
        // -------
        JavaRDD<String> lines = spark.read().textFile(args[0]).javaRDD();

        // -------
        // Do transformations
        // -------

        // Each line is split into words using flatMap transformation
        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());

        // Assign the value ‘1’ to each of the work-keys using mapToPairs transformation
        JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<>(s, 1));

        // Add values of similar keys to get the final word count using reduceByKey transformation
        JavaPairRDD<String, Integer> wordCounts = ones.reduceByKey((i1, i2) -> i1 + i2);

        // -------
        // Perform actions
        // -------

        // Find total number of words
        long totalWords = wordCounts.count();
        System.out.println("*** Total words = " + totalWords);

        // Return all elements of RDD using collect action and print
        System.out.println("*** RDD collect action:");
        List<Tuple2<String, Integer>> output = wordCounts.collect();

        for (Tuple2<?,?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }

        // Print word count result using foreach action on RDD
        System.out.println("*** RDD foreach action:");
        wordCounts.foreach(tuple -> System.out.println(tuple._1() + ": " + tuple._2()));

        // Done
        spark.stop();
    }
}
