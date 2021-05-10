# Getting Started with Spark using Java

Sample code to play with Spark using Java

## Required Software and Setup

* Install Java 11 or later
* Install Maven
* Setup Spark (e.g. spark-3.3.1-bin-hadoop3.2) and configure necessary environment variables (e.g. 'SPARK_HOME' and include '%SPARK_HOME%/bin' in path) 
* Setup Hadoop winutils in case of Window OS and configure related environment variable (e.g. 'HADOOP_HOME' and include '%HADOOP_HOME%/bin' in 'path')
* Install Intellij 2018.3+ and open the 'gs-spark-java' maven project in IDE

## Word Count Example using Spark RDD

* See example java code of [Word count using Spark RDD](src/main/java/com/tirthal/learning/example/WorkCountWithSparkRdd.java) to understand Spark RDD (i.e. create RDD from input text file, do transformations and perform actions)

* __Run using Intellij locally__: Select 'WordCountWithSparkRdd.java' - Press Alt+Shift+F10 - Edit Configuration - Configuration - Give location of any text file in 'Program arguments' - Run 

* __Run Spark job locally (with as many worker threads as logical cores on your machine) using spark-submit__: Go to 'gs-spark-java' project directory, build project using maven (i.e. `mvn clean package`) and submit to local spark cluster: 

    `spark-submit --master local[*] --class com.tirthal.learning.example.WordCountWithSparkRdd --packages org.apache.commons:commons-csv:1.2 target/gs-spark-java-1.0-SNAPSHOT.jar <path-of-text-file>`     

* __Run Spark job on Yarn using spark-submit__: Go to 'gs-spark-java' project directory, build project using maven (i.e. `mvn clean package`), upload "target/gs-spark-java-1.0-SNAPSHOT.jar" to `/tmp/spark-poc` HDFS directory and submit to a Yarn cluster: 

    `spark-submit --master yarn --class com.tirthal.learning.example.WordCountWithSparkRdd --packages org.apache.commons:commons-csv:1.2 --deploy-mode cluster /tmp/spark-poc/gs-spark-java-1.0-SNAPSHOT.jar <path-of-text-file>`
    
Some of the commonly used options of "spark-submit" are:

* __--class__: The entry point for your application (e.g. org.apache.spark.examples.SparkPi).
* __--master__: The master URL for the cluster (e.g. spark://a.b.c.d:xxxx).
* __--packages__: Used to include dependency (e.g. common csv) as a runtime dependency. Remember that there is no need to include Spark itself as a dependency since it is implied by default. More than one dependencies can be added as a comma-separated list of Maven IDs.
* __--deploy-mode__: Whether to deploy your driver on the worker nodes (cluster) or locally as an external client (client) (default: client).
* __--conf__: Arbitrary Spark configuration property in key=value format. For values that contain spaces wrap “key=value” in quotes (as shown).
* __application-jar__: Path to a bundled jar including your application and all dependencies. The URL must be globally visible inside of your cluster, for instance, an hdfs:// path or a file:// path that is present on all nodes.
* __application-arguments__: Arguments passed to the main method of your main class, if any.

## Also Try

* [Submitting Spark Applications on - a Spark standalone cluster | a YARN cluster | a Mesos cluster | a Kubernetes cluster](https://spark.apache.org/docs/latest/submitting-applications.html)
* Submitting Spark job on Google Cloud Dataproc: [Create a cluster](https://cloud.google.com/dataproc/docs/guides/create-cluster), [Submit a Spark job](https://cloud.google.com/dataproc/docs/guides/submit-job), [Manage Java and Scala dependencies for Apache Spark](https://cloud.google.com/dataproc/docs/guides/manage-spark-dependencies)
* [Submitting Spark step on AWS EMR](https://docs.aws.amazon.com/emr/latest/ReleaseGuide/emr-spark-submit-step.html)