package utils

import java.lang.management.ManagementFactory

import config.Settings
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object SparkUtils {

  // SparkUtils config
  val suc = Settings.Spark

  val isIDE = {
    ManagementFactory.getRuntimeMXBean.getInputArguments.toString.contains("IntelliJ IDEA")
  }

  def getSparkContext(appName: String) = {
    var checkpointDirectory = ""

    // get spark configuration
    val conf = new SparkConf()
      .setAppName(appName)

    // Check if running from IDE
    if (isIDE) {
      System.setProperty("hadoop.home.dir", suc.winutilsDirectoryForIde) // required for winutils
      conf.setMaster("local[*]")
      checkpointDirectory = suc.checkpointDirectoryForIde
    } else {
      checkpointDirectory = suc.checkpointDirectoryForHdfs
    }

    // setup spark context
    val sc = SparkContext.getOrCreate(conf)
    sc.setCheckpointDir(checkpointDirectory)
    sc
  }

  def getSQLContext(sc: SparkContext) = {
    val sqlContext = SQLContext.getOrCreate(sc)
    sqlContext
  }

  def getStreamingContext(streamingApp : (SparkContext, Duration) => StreamingContext, sc : SparkContext, batchDuration: Duration) = {
    val creatingFunc = () => streamingApp(sc, batchDuration)
    val ssc = sc.getCheckpointDir match {
      case Some(checkpointDir) => StreamingContext.getActiveOrCreate(checkpointDir, creatingFunc, sc.hadoopConfiguration, createOnError = true)
      case None => StreamingContext.getActiveOrCreate(creatingFunc)
    }
    sc.getCheckpointDir.foreach( cp => ssc.checkpoint(cp))
    ssc
  }

  /*
    What's Checkpoint in Spark?

    The need with Spark Streaming application is that it should be operational 24/7. Thus, the system should also be fault tolerant.
    If any data is lost, the recovery should be speedy. Spark streaming accomplishes this using checkpointing. So, Checkpointing is a
    process to truncate RDD lineage graph. It saves the application state timely to reliable storage (HDFS). As the driver restarts
    the recovery takes place. There are two types of data that we checkpoint in Spark: Metadata Checkpointing and Data Checkpointing.

    To set the Spark checkpoint directory call: SparkContext.setCheckpointDir(directory: String)
  */
}
