package clickstream

import java.util.Properties

import config.Settings
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}

import scala.util.Random

/**
  * Kafka producer - sending clickstream records to "weblogs-text" topic of Kafka
  */
object LambdaKafkaProducer extends App {

  val wlc = Settings.WebLogGen
  val lc = Settings.Lambda

  val Products = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/products.csv")).getLines().toArray
  val Referrers = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/referrers.csv")).getLines().toArray
  val Visitors = (0 to wlc.visitors).map("Visitor-" + _)
  val Pages = (0 to wlc.pages).map("Page-" + _)
  val rnd = new Random()

  // ----------------------------------------------------------
  // -----------  Kafka Producer Configuration ----------------
  // ----------------------------------------------------------
  val topic = lc.kafkaTopic
  val props = new Properties()

  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, lc.kafkaServerConfig)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.ACKS_CONFIG, "all")
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "WebLogProducer")

  val kafkaProducer: Producer[Nothing, String] = new KafkaProducer[Nothing, String](props)
  println(kafkaProducer.partitionsFor(topic))
  // ----------------------------------------------------------
  // -----------  Kafka Producer Configuration ----------------
  // ----------------------------------------------------------

  for (_ <- 1 to wlc.numberOfFiles) {

    // Produce clickstream record

    val incrementTimeEvery = rnd.nextInt(wlc.records - 1) + 1

    var timestamp = System.currentTimeMillis()
    var adjustedTimestamp = timestamp

    for (iteration <- 1 to wlc.records) {
      adjustedTimestamp = adjustedTimestamp + ((System.currentTimeMillis() - timestamp) * wlc.timeMultiplier)
      timestamp = System.currentTimeMillis() // move all this to a function
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

      // ------------------------------------------------------------------------------------
      // -----------  Send clickstream record to "weblogs-text" Kafka Topic -----------------
      // ------------------------------------------------------------------------------------
      val producerRecord = new ProducerRecord(topic, line)
      kafkaProducer.send(producerRecord)
      // ------------------------------------------------------------------------------------
      // -----------  Send clickstream record to "weblogs-text" Kafka Topic -----------------
      // ------------------------------------------------------------------------------------

      // Random wait period
      if (iteration % incrementTimeEvery == 0) {
        println(s"Sent $iteration messages!")
        val sleeping = rnd.nextInt(incrementTimeEvery * 60)
        println(s"Sleeping for $sleeping ms")
        Thread sleep sleeping
      }
    }

    val sleeping = 2000
    println(s"Sleeping for $sleeping ms")
  }

  kafkaProducer.close()
}