package config

import com.typesafe.config.ConfigFactory

/**
  * A singleton class instance responsible to load properties from config file (i.e. resources/application.conf)
  */
object Settings {

  // A way to load config file in Scala
  private val config = ConfigFactory.load()

  // A parent singleton wrapper for "clickstream" properties
  object WebLogGen {
    private val weblogGen = config.getConfig("clickstream")

    lazy val records = weblogGen.getInt("records")
    lazy val timeMultiplier = weblogGen.getInt("time_multiplier")
    lazy val pages = weblogGen.getInt("pages")
    lazy val visitors = weblogGen.getInt("visitors")
    lazy val filePath = weblogGen.getString("file_path")
    lazy val destPath = weblogGen.getString("dest_path")
    lazy val numberOfFiles = weblogGen.getInt("number_of_files")
  }

  // A parent singleton wrapper for "batchjob" properties
  object SparkBatchJob {
    private val sparkBatchJob = config.getConfig("batchjob")

    lazy val sourceFileForIde = sparkBatchJob.getString("local_ide_sourceFile_path")
    lazy val sourceFileForVagrant = sparkBatchJob.getString("vagrant_vm_sourceFile_path")
  }

  // A parent singleton wrapper for "sparkutils" properties
  object Spark {
    private val sparkUtils = config.getConfig("sparkutils")

    lazy val winutilsDirectoryForIde = sparkUtils.getString("local_ide_winutils_directory")
    lazy val checkpointDirectoryForIde = sparkUtils.getString("local_ide_checkpoint_directory")
    lazy val checkpointDirectoryForHdfs = sparkUtils.getString("vagrant_hdfs_checkpoint_directory")
  }
}
