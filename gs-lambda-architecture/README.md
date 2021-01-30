# Getting Started with Big Data Lambda Architecture with Spark, Kafka, and Cassandra

This contains my learning code of [Pluralsight course: Applying the Lambda Architecture with Spark, Kafka, and Cassandra](cture/table-of-contents), which I produced as per instructions imparted during the course and included some additional code changes, experiments and notes. 

The course introduces how to build robust, scalable, real-time big data systems using a variety of Apache Spark's APIs, including the Streaming, DataFrame, SQL, and DataSources APIs, integrated with Apache Kafka, HDFS and Apache Cassandra.

* **Lambda Architecture**: It is a data processing architecture and framework designed to address robustness of the scalability and fault-tolerance (human and machine) of big data systems by balancing between latency (timeliness of results from data processing) and accuracy of results.
* **Apache HDFS**: The Hadoop Distributed File System (HDFS) is a distributed file system designed to run on commodity hardware. It is highly fault-tolerant and is designed to be deployed on low-cost hardware. It provides high throughput access to application data and is suitable for applications that have large data sets. 
* **Apache Spark**: Is it a unified analytics engine for large-scale data processing which achieves high performance for both batch and streaming data, using a state-of-the-art DAG scheduler, a query optimizer, and a physical execution engine. It runs on Hadoop, Apache Mesos, Kubernetes, standalone, or in the cloud. It can access diverse data sources.
* **Apache Kafka**: It is used as a distributed publish-subscribe messaging system / distributed streaming platform. In other words, it is used for building real-time data pipelines and streaming apps. It is horizontally scalable, fault-tolerant, wicked fast, and runs in production in thousands of companies.
* **Apache Cassandra**: It is a distributed database management system designed for large amounts of data, providing high availability with no single point of failure.
* **Apache Zeppelin**: It is a web-based notebook that enables data-driven, interactive data analytics and collaborative documents with SQL, Scala and more.

## Environment setup

Install following software followed by workshop VM setup on local development machine.

### Software Installation

* Java 8 Oracle SDK
* Intellij (+ install Scala plugin)
* [Oracle VirtualBox 5.0.14 or above](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant 1.8.1 or above](https://www.vagrantup.com/downloads.html)
* For Windows OS : Cygwin (Linux like shell on Windows), ssh client and rsync installation using Cholatey
    - Install [chocolatey](https://chocolatey.org/) and run below commands
    - choco install cyg-get
    - cyg-get opensshhttps://app.pluralsight.com/library/courses/spark-kafka-cassandra-applying-lambda-archite
    - cyg-get rsync
* [winutils](http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe) to run Spark program using Intellij IDE on Windows OS (not required, if executing the program on YARN using spark-submit command)

### Workshop VM Setup

* Add "127.0.0.1    lambda-pluralsight" entry in OS's host file (In case of Windows OS: C:\Windows\System32\drivers\etc\hosts)
* Ensure to keep enough space in default machine folder (`Oracle VM VirtualBox -> File -> Preferences - General - e.g. d:\x-temp\vagrantvm\virtualbox-default`), because it'll require to store 8GB of downloaded VM image
* Open Cygwin and go to directory where want to store the Vagrant box and clone project from Github
    - Open cygwin terminal: `cygwin`
    - Go to choice of directory (e.g.`cd /cygdrive/d/x-temp/vagrantvm`) and clone the workshop VM Repository from the Github: `git clone https://github.com/aalkilani/spark-kafka-cassandra-applying-lambda-architecture`
* Manage workshop VM via Vagrant
    - Go to vagrant directory in the cloned repository (e.g. `cd /cygdrive/d/x-temp/vagrantvm/spark-kafka-cassandra-applying-lambda-architecture/vagrant`) 
    - Download vagrant box VM (if doesn't exist locally) and start or restart it: `vagrant up`
    - SSH to VM: `vagrant ssh`
    - SSH to VM and go to shared directory inside the VM (which is mounted as shared directory between VM and the vagrant directory in the cloned repository on Host OS): `cd \vagrant`
    - SSH to VM and run docker commands: `docker ps -a` | `docker restart <container-name>` | ...    
    - Ensure cassandra, zeppelin, spark, zookeeper and kafka docker containers are in running status in VM using "docker ps -a" command
* Few useful vagrant commands:        
    - See state of all active Vagrant environments on the system for the currently logged in user: `vagrant global-status`
    - Reload VM (= 'vagrant halt' followed by 'vagrant up'): `vagrant reload`
    - Shutdown VM forcefully: `vagrant halt`
    - Suspend and Resume VM: `vagrant suspend` and `vagrant resume`

### Key URLs 

* [Apache Zeppeline](http://localhost:8988/)
* [Hadoop cluster](http://localhost:8088/cluster)
* [Name Node UI](http://localhost:50070/dfshealth.html)
* [Local Spark UI](http://localhost:4040)

## Key Steps of Hands-on Workshop 

Import the project in IDE (e.g. Intellij) and follow instructions as below to practically learn, which anticipates some proficiency of Scala, Intellij, Vagrant, Docker, etc.

In case of any issue with workshop VM (i) see [troubleshooting guide](https://github.com/aalkilani/spark-kafka-cassandra-applying-lambda-architecture/blob/master/vagrant/troubleshooting.md) (ii) try reloading VM: `vagrant reload`

### Getting Started with Spark with Zeppeline

* Start Workshop VM via vagrant (if not yet) and Open Zeppeline
* Create new note and perform steps as per "01-spark-intro.txt" | Import note - Choose a json "01-Spark Intro.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)
* Go to [Interpreter](http://localhost:8988/#/interpreter) : Spark : Edit : Add below dependencies and Save : Restart Interpreter
    - org.apache.kafka:kafka_2.11:0.8.2.1
    - org.apache.spark:spark-streaming-kafka-0-8_2.11:2.0.2
    - com.twitter:algebird-core_2.11:0.12.4

### Getting Started with Batch Layer using Apache Spark

* **FirstLogProducer.scala - How to produce clickstream dataset?** (e.g. "data.tsv" and ".../input/data_*" files)
    - Configure "clickstream.file_path" (to be used by FirstBatchJobUsingSparkRDD / FirstBatchJobUsingSparkDF) and "clickstream.dest_path" (to be used by FirstStreamingJobUsingSpark) in "application.conf" to match vagrant folder location of "spark-kafka-cassandra-applying-lambda-architecture" project  
    - Select "FirstLogProducer.scala" (i.e. spark-lambda\src\main\scala\clickstream directory) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - See the "clickstream.dest_path" folder which should have 50 "data_*" files and the "clickstream.file_path" file (each file containing 200 records of clickstream dataset)

* **FirstBatchJobUsingSparkRDD.scala (First Batch Job of Aggregating with Spark RDD API) | FirstBatchJobUsingSparkDF.scala (First Batch Job of Aggregating with Spark DataFrame API) - How to run first batch job locally using Intellij IDE**?    
    - Configure "batchjob.local_ide_sourceFile_path", "sparkutils.local_ide_winutils_directory" and "sparkutils.local_ide_checkpoint_directory" in "application.conf"
    - Compare FirstBatchJobUsingSparkRDD.scala vs. FirstBatchJobUsingSparkDF.scala to understand Spark RDD API vs. DataFrame API usage 
    - Select "FirstBatchJobUsingSparkRDD.scala | FirstBatchJobUsingSparkDF.scala" (spark-lambda\src\main\scala\batch directory) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - See printed information in logs of IDE and Spark jobs progress on SparkUI (i.e. find "http://virtual-box-host-ip:4040" url in "SparkUI: Started SparkUI at..." log) while program is executing
    
* **FirstBatchJobUsingSparkDF.scala - How to save to HDFS and execute on YARN (Spark Scheduling with Cluster Deploy Mode)?**
    - Configure "batchjob.vagrant_vm_sourceFile_path" and "sparkutils.vagrant_hdfs_checkpoint_directory" in "application.conf"
    - Package "spark-lambda" module using Maven (e.g. Intellij - View - Tool Windows - Mavan Projects) and copy shaded fat jar (e.g. spark-lambda/target/spark-lambda-1.0-SNAPSHOT-shaded.jar) to "vagrant/data" directory (e.g. .../spark-kafka-cassandra-applying-lambda-architecture/vagrant/data)
    - Start Workshop VM via vagrant (if not yet), SSH to vagrant and go to spark directory: cd /pluralsight/spark/
    - Run spark-submit command: `./bin/spark-submit --master yarn --deploy-mode cluster --class batch.FirstBatchJobUsingSparkDF /vagrant/data/spark-lambda-1.0-SNAPSHOT-shaded.jar`
    - See Hadoop cluster UI
    - Check batch job write result in "/lambda/batch1" directory using one of below options
        - Open Name Node UI and Search "/lambda" directory using "Utilities : Browse file system" option
        - SSH to vagrant and run this hdfs command: `hdfs dfs -ls /pluralsight/spark/lambda/batch1`
      
* **Querying Data using Zeppeline with Spark Data Sources API**
    - Start Workshop VM via vagrant (if not yet) and Open Zeppeline
    - Create new note and perform steps as per "02-spark-datasources-api.txt" | Import note - Choose a json "02-spark-datasources-api.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)
    
### Getting Started with Speed Layer using Spark Streaming
     
* **FirstStreamingJobUsingSpark.scala (First Streaming Job of Aggregating with Spark DStream API + Window Operations, Stateful Transformation, Cardinality Estimation using HyperLogLog) - How to run streaming job locally using Intellij IDE?** 
    - Configure "streamingjob.local_ide_sourceFile_path", "sparkutils.local_ide_winutils_directory" and "sparkutils.local_ide_checkpoint_directory" in "application.conf"
    - Select "FirstStreamingJobUsingSpark.scala (spark-lambda\src\main\scala\streaming directory) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - Select "FirstLogProducer.scala" (spark-lambda\src\main\scala\clickstream directory) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - See printed information in logs of IDE and "clickstream.dest_path" directory, while both programs are executing 

* **Trying Spark Streaming Aggregation and HyperLogLog using Zeppeline**
    - Start Workshop VM via vagrant (if not yet) and Open Zeppeline
    - Import note : Choose a json "03-Streaming Aggregations.json" and "04-HyperLogLog.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)    
    - Run the "FirstLogProducer.scala" program followed by all paragraphs of the "Streaming Aggregations" note
    - Go to Zeppeline Interpreter : Spark : Edit : Add "com.twitter:algebird-core_2.11:0.12.4" dependencies and Save (if doesn't exist) : Restart Interpreter
    - Run the "FirstLogProducer.scala" program followed by all paragraphs of the "HyperLogLog" note 

## Final Big Data Lambda Architecture 

Start Workshop VM via vagrant (if not yet) and perform following steps in given sequence.

* **LambdaKafkaProducer.scala - Kafka Producer to send clickstream records to "weblogs-text" Topic of Kafka**    
    - Configure "lambda.kafka_server_config" in "application.conf", if require
    - Select "LambdaKafkaProducer.scala" (i.e. spark-lambda\src\main\scala\clickstream directory) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - SSH to vagrant and run following commands to confirm clickstream is getting stored in Kafka topic:
        - Confirm the program created "weblogs-text" topic: `docker exec -it kafka kafka-topics --list --zookeeper localhost:2181` | `docker exec -it kafka kafka-topics --describe --zookeeper localhost:2181`
        - Check total messages count for the "weblogs-text" topic (sent by the kafka producer): `docker exec -it kafka kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic weblogs-text --time -1 --offsets 1 | awk -F ':' '{sum += $3} END {print sum}'`
        - Consume messages for the "weblogs-text" topic: `docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --zookeeper localhost:2181 --topic weblogs-text --from-beginning`

* **Trying Spark Kafka Integration with Receiver Based Approach using Zeppeline**    
    - Understand [Spark Streaming Kafka Integration - Receiver and Direct Approaches](https://www.supergloo.com/fieldnotes/spark-streaming-kafka-example/)
    - Open Zeppelinea and Import note : Choose a json "05-Kafka.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)
    - Go to Zeppeline Interpreter : Spark : Edit : Add "org.apache.kafka:kafka_2.11:0.8.2.1" and "org.apache.spark:spark-streaming-kafka-0-8_2.11:2.0.2" dependencies and Save (if don't exist) : Restart Interpreter
    - Run the "LambdaKafkaProducer.scala" program followed by all paragraphs of the "Kafka" note

* **Creating Keyspace and Tables of Cassandra using Zeppeline**
    - Open Zeppeline and Import note : Choose a json "06-Cassandra.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)     
    - Run all paragraphs of the "Cassandra" note

* **LambdaStreamingJob.scala - Streaming Ingest to HDFS using Spark Streaming Kafka Integration with Direct Approach (Speed Layer) + Persisting Spark Streaming Realtime Views in Cassandra (Serving layer)** 
    - Configure "lambda.hdfs_path" in "application.conf", if require
    - Package "spark-lambda" module using Maven (e.g. Intellij - View - Tool Windows - Mavan Projects) and copy shaded fat jar (e.g. spark-lambda/target/spark-lambda-1.0-SNAPSHOT-shaded.jar) to "vagrant/data" directory (e.g. .../spark-kafka-cassandra-applying-lambda-architecture/vagrant/data)
    - SSH to vagrant and go to spark directory: cd /pluralsight/spark/
    - Run spark-submit command: `./bin/spark-submit --master yarn --deploy-mode cluster --class streaming.LambdaStreamingJob /vagrant/data/spark-lambda-1.0-SNAPSHOT-shaded.jar`        

* **LambdaBatchJob.scala - Batch Processing from HDFS (Batch Layer) + Persisting Spark Batch Views in Cassandra (Serving layer)**
    - SSH to vagrant and go to spark directory: cd /pluralsight/spark/
    - Run spark-submit command: `./bin/spark-submit --master yarn --deploy-mode cluster --class batch.LambdaBatchJob /vagrant/data/spark-lambda-1.0-SNAPSHOT-shaded.jar`    
    
* **Running CQL using Zeppeline to query Realtime views and Batch views from Cassandra (Serving layer)**
    - See Realtime views: Open "Cassandra" note and run last two paragraphs (i.e. select for lambda.stream_activity_by_product and lambda.stream_visitors_by_product)
    - See Batch views: Open "Batch to Cassandra" note and run last paragraph (i.e. select for lambda.batch_activity_by_product and lambda.batch_visitors_by_product) 

## Disclaimer

I recorded above instructions from the [Plural course: Applying the Lambda Architecture with Spark, Kafka, and Cassandra](https://app.pluralsight.com/library/courses/spark-kafka-cassandra-applying-lambda-architecture/table-of-contents) for my own quick reference. If anyone else wants to understand this subject, I strongly recommend to attend the course on pluralsight.