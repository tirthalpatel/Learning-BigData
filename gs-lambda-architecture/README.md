# Getting Started with Big Data Lambda Architecture with Spark, Kafka, and Cassandra

This contains learning code of [Pluralsight course: Applying the Lambda Architecture with Spark, Kafka, and Cassandra](https://app.pluralsight.com/library/courses/spark-kafka-cassandra-applying-lambda-architecture/table-of-contents), which I produced as per instructions imparted during the course along with some additional self code changes and notes. 

The course introduces how to build robust, scalable, real-time big data systems using a variety of Apache Spark's APIs, including the Streaming, DataFrame, SQL, and DataSources APIs, integrated with Apache Kafka, HDFS and Apache Cassandra.

## Environment setup

Install following software followed by workshop VM setup.

### Software Installation

* Java 8 Oracle SDK
* Intellij (+ install Scala plugin)
* [Oracle VirtualBox 5.0.14 or above](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant 1.8.1 or above](https://www.vagrantup.com/downloads.html)
* For Windows OS : Cygwin (Linux like shell on Windows), ssh client and rsync installation using Cholatey
    - Install [chocolatey](https://chocolatey.org/) and run below commands
    - choco install cyg-get
    - cyg-get openssh
    - cyg-get rsync
* [winutils](http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe) to run Spark program using Intellij IDE on Windows OS (not required, if executing the program on YARN using spark-submit command)

### Workshop VM Setup

* Add "127.0.0.1    lambda-pluralsight" entry in OS's host file (In case of Windows OS: C:\Windows\System32\drivers\etc\hosts)
* Ensure to keep enough space in default machine folder (`Oracle VM VirtualBox -> File -> Preferences - General - e.g. e:\x-temp\VMs\VirtualBox-VMs`), because it'll require to store 8GB of downloaded VM image
* Open Cygwin and go to directory where want to store the Vagrant box and clone project from Github
    - Open cygwin terminal: `cygwin`
    - Go to Default Machine Folder via Cygwin: `cd /cygdrive/e/x-temp/VMs/VirtualBox-VMs`
    - Clone the workshop VM Repository from the Github: `git clone https://github.com/aalkilani/spark-kafka-cassandra-applying-lambda-architecture`
* Manage workshop VM via Vagrant
    - Go to vagrant directory in the cloned repository: `cd /cygdrive/e/x-temp/VMs/VirtualBox-VMs/spark-kafka-cassandra-applying-lambda-architecture/vagrant`    
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
* [Name Node UI](http://lambda-pluralsight:50070/dfshealth.html)

## Learning Objectives

Import the project in IDE (e.g. Intellij) and follow instructions as below to practically learn.

* **Lambda Architecture**: It is a data processing architecture and framework designed to address robustness of the scalability and fault-tolerance (human and machine) of big data systems by balancing between latency (timeliness of results from data processing) and accuracy of results.
* **Apache HDFS**: The Hadoop Distributed File System (HDFS) is a distributed file system designed to run on commodity hardware. It is highly fault-tolerant and is designed to be deployed on low-cost hardware. It provides high throughput access to application data and is suitable for applications that have large data sets. 
* **Apache Spark**: Is it a unified analytics engine for large-scale data processing which achieves high performance for both batch and streaming data, using a state-of-the-art DAG scheduler, a query optimizer, and a physical execution engine. It runs on Hadoop, Apache Mesos, Kubernetes, standalone, or in the cloud. It can access diverse data sources.
* **Apache Zeppelin**: It is a web-based notebook that enables data-driven, interactive data analytics and collaborative documents with SQL, Scala and more.

### Spark with Zeppeline Introduction

* Open Zeppeline
* Create new note and perform steps as per "01-spark-intro.txt" | Import note - Choose a json "01-Spark Intro.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)

### Batch Layer with Apache Spark

* **LogProducer.scala - How to produce clickstream dataset?** (e.g. an input log files ".../input/data_..." containing clickstream dataset)
    - Configure "clickstream.file_path" and "clickstream.dest_path" in "application.conf" to match vagrant folder location of "spark-kafka-cassandra-applying-lambda-architecture" project  
    - Select LogProducer (i.e. spark-lambda\src\main\scala\clickstream\LogProducer.scala) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - See the "clickstream.dest_path" folder which should have 50 "data_*" files and the "clickstream.file_path" file (each file containing 200 records of clickstream dataset)

* **BatchJobUsingSparkRDD.scala (First Batch Job of Aggregating with Spark RDD API) | BatchJobUsingSparkDF.scala (First Batch Job of Aggregating with Spark DataFrame API) - How to run first batch job locally using Intellij IDE**?    
    - Configure "batchjob.local_ide_sourceFile_path", "sparkutils.local_ide_winutils_directory" and "sparkutils.local_ide_checkpoint_directory" in "application.conf"
    - Compare BatchJobUsingSparkRDD.scala vs. BatchJobUsingSparkDF.scala to understand Spark RDD API vs. DataFrame API usage 
    - Select "BatchJobUsingSparkRDD.scala | BatchJobUsingSparkDF.scala" (spark-lambda\src\main\scala\batch directory) in Intellij IDE and run it (click Ctrl+Shift+F10)
    - See printed information in logs of IDE and Spark jobs progress on SparkUI (i.e. find "http://virtual-box-host-ip:4040" url in "SparkUI: Started SparkUI at..." log) while program is executing
    
* **BatchJobUsingSparkDF.scala - How to save to HDFS and execute on YARN (Spark Scheduling with Cluster Deploy Mode)?**
    - Configure "batchjob.vagrant_vm_sourceFile_path" and "sparkutils.vagrant_hdfs_checkpoint_directory" in "application.conf"
    - Package "spark-lambda" module using Maven (e.g. Intellij - View - Tool Windows - Mavan Projects) and copy shaded fat jar (e.g. spark-lambda/target/spark-lambda-1.0-SNAPSHOT-shaded.jar) to "vagrant" directory (e.g. .../spark-kafka-cassandra-applying-lambda-architecture/vagrant)
    - SSH to vagrant and go to spark directory: cd /pluralsight/spark/
    - Run spark-submit command: ./bin/spark-submit --master yarn --deploy-mode cluster --class batch.BatchJobUsingSparkDF /vagrant/spark-lambda-1.0-SNAPSHOT-shaded.jar
    - See Hadoop cluster
    - Check batch job write result in "/lambda/batch1" directory using one of below options
        - Open Name Node UI and Search "/lambda" directory using "Utilities : Browse file system" option
        - SSH to vagrant and run this hdfs command: hdfs dfs -ls /pluralsight/spark/lambda/batch1
      
* **Querying Data using Zeppeline with Spark Data Sources API**
    - Open Zeppeline
    - Create new note and perform steps as per "02-spark-datasources-api.txt" | Import note - Choose a json "02-spark-datasources-api.json" (see "gs-lambda-architecture\spark-lambda\data\zeppeline\" folder)
     
## Disclaimer

I recorded above instructions from the [Plural course: Applying the Lambda Architecture with Spark, Kafka, and Cassandra](https://app.pluralsight.com/library/courses/spark-kafka-cassandra-applying-lambda-architecture/table-of-contents) for my own quick reference. If anyone else wants to understand this subject, I strongly recommend to attend the course on pluralsight.