package clickstream

import java.io.FileWriter
import config.Settings
import org.apache.commons.io.FileUtils
import scala.util.Random

/**
  * Executable program to produce clickstream dataset (i.e. "data.tsv" and ".../input/data_*" files as per configuration in "application.conf")
  */
// An object in Scala = A class that has exactly one instance (i.e. Scala's ability for singleton pattern).
// An App in Scala = A trait that can be used to quickly turn objects into executable programs. Here, object Main inherits the main method of App.
object FirstLogProducer extends App {
  // WebLog config
  val wlc = Settings.WebLogGen

  // Build reference data container to be used while producing clickstream data
  val Products = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/products.csv")).getLines().toArray
  val Referrers = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/referrers.csv")).getLines().toArray
  val Visitors = (0 to wlc.visitors).map("Visitor-" + _)
  val Pages = (0 to wlc.pages).map("Page-" + _)
  val rnd = new Random()

  // What's log file path config
  val filePath = wlc.filePath
  val destPath = wlc.destPath

  // Produce number of files in dest path as per config
  for (fileCount <- 1 to wlc.numberOfFiles) {

    val fw = new FileWriter(filePath, true)

    // introduce some randomness to time increments for demo purposes
    val incrementTimeEvery = rnd.nextInt(wlc.records - 1) + 1

    var timestamp = System.currentTimeMillis()
    var adjustedTimestamp = timestamp

    // Each file should contain number of records as per config
    for (iteration <- 1 to wlc.records) {
      // Produce clickstream record
      adjustedTimestamp = adjustedTimestamp + ((System.currentTimeMillis() - timestamp) * wlc.timeMultiplier)
      timestamp = System.currentTimeMillis()
      val action = iteration % (rnd.nextInt(200) + 1) match {
        case 0 => "purchase"
        case 1 => "add_to_cart"
        case _ => "page_view"
      }
      val referrer = Referrers(rnd.nextInt(Referrers.length - 1))
      val prevPage = referrer match {
        case "Internal" => Pages(rnd.nextInt(Pages.length - 1))
        case _ => ""
      }
      val visitor = Visitors(rnd.nextInt(Visitors.length - 1))
      val page = Pages(rnd.nextInt(Pages.length - 1))
      val product = Products(rnd.nextInt(Products.length - 1))

      val line = s"$adjustedTimestamp\t$referrer\t$action\t$prevPage\t$visitor\t$page\t$product\n"

      // Write clickstream record to file
      fw.write(line)

      if (iteration % incrementTimeEvery == 0) {
        println(s"Sent $iteration messages!")
        val sleeping = rnd.nextInt(incrementTimeEvery * 60)
        println(s"Sleeping for $sleeping ms")
        Thread sleep sleeping // Alternative syntax = Thread.sleep(sleeping)
      }
    }
    fw.close()

    val outputFile = FileUtils.getFile(s"${destPath}data_$timestamp")

    println(s"Moving produced data to $outputFile")
    if (fileCount == wlc.numberOfFiles)
      FileUtils.copyFile(FileUtils.getFile(filePath), outputFile) // Keep produced last file (e.g. data.tsv)
    else
      FileUtils.moveFile(FileUtils.getFile(filePath), outputFile) // Move produced data to dest path (e.g. ../input/data_*)

    val sleeping = 5000
    println(s"Sleeping for $sleeping ms")
  }
}
