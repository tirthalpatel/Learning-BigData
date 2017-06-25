# Getting Started with [Apache Kafka](http://kafka.apache.org/)

- Kafka project aims to provide a unified, high-throughput, low-latency platform for handling real-time data feeds
- A distributed streaming platform capable of handling hundreds of megabytes of reads and writes per second from thousands of clients + Built-in partitioning, replication, and fault-tolerance
- There are four main components involved in moving data in and out of Kafka: Topics, Producers, Consumers and Brokers
- Use cases: Stream Processing, Event Sourcing, Commit Log, Messaging, Web Activity Tracking, Metrics, Log Aggregation...
	- Kafka as a Messaging System: publish and subscribe to streams of data 
	- Kafka for Stream Processing: process streams of data efficiently and in real time 
	- Kafka as a Storage System: store streams of data in a distributed replicated cluster

## Installation Options

Consider suitable option to install and run Kafka.

- [Kafka Quickstart](http://kafka.apache.org/quickstart) / [A step-by-step tutorial on how to install and run Apache Kafka on Windows](https://www.codenotfound.com/2016/09/apache-kafka-download-installation.html)
	- [Download Kafka](http://kafka.apache.org/downloads) & un-tar it
	- Go to "conf" directory: Configure properties as per need, e.g. 'dataDir' in 'zookeeper.properties' and 'log.dirs' in 'server.properties'
	- Start zookeeper server: `bin\windows\zookeeper-server-start.bat config\zookeeper.properties`
	- Start kafka server: `bin\windows\kafka-server-start.bat config\server.properties`
	- Try to set up a multi-broker cluster, create topic, send message, consume message...
- [Multi-broker Apache Kafka set-up using Docker Compose](https://hub.docker.com/r/wurstmeister/kafka/)
- [Confluent Kafka Platform](https://www.confluent.io/product/compare/)
- [Hortonworks Kafka Release](https://hortonworks.com/apache/kafka/) 

## Sample POC Apps

Refer code examples of Kafka client applications such as a Java producer application (for writing data to Kafka) and a Java consumer application (for reading data from Kafka). Also, learn to implement real-time processing applications using Kafka's Streams API aka "Kafka Streams".

- [Sample Kakfa client application code to send or receive String or JSON messages using Spring Kafka, Spring Boot and Maven](https://github.com/tirthalpatel/Learning-Spring/tree/master/gs-spring-kafka/poc-spring-boot-kafka-app)
- [Code examples for Apache Kafka and Confluent Platform](https://github.com/confluentinc/examples/tree/master)

## Also Refer

- [Getting Started with Apache Kafka](https://www.confluent.io/blog/apache-kafka-getting-started/)
- [Kafka Ecosystem](https://cwiki.apache.org/confluence/display/KAFKA/Ecosystem)
- [Apache Kafka: Next Generation Distributed Messaging System](https://www.infoq.com/articles/apache-kafka)
- [Kafka Consumer Client Deep Dive](https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/)
- [Kafka Blogs](https://www.confluent.io/blog/)