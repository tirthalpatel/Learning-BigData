# Getting Started with Spark using Java

Sample code to play with Spark using Java

## Required Software and Setup

* Install Java 8 or later
* Install Maven
* Setup Spark (e.g. spark-2.3.1-bin-hadoop2.7) and configure necessary environment variables (e.g. 'SPARK_HOME' and include '%SPARK_HOME%/bin' in path) 
* Setup Hadoop winutils in case of Window OS (e.g. hadoop-2.7.1-winutils) and configure related environment variable (e.g. 'HADOOP_HOME' and include '%HADOOP_HOME%/bin' in 'path')
* Install Intellij 2018.3+ and open the 'gs-spark-java' maven project in IDE

## Word Count Example using Spark RDD

* See example java code of [Word count using Spark RDD](src/main/java/com/tirthal/learning/example/WorkCountWithSparkRdd.java) to understand Spark RDD (i.e. create RDD from input text file, do transformations and perform actions)
* __Run using Intellij locally__: Select 'WordCountWithSparkRdd.java' - Press Alt+Shift+F10 - Edit Configuration - Configuration - Give location of any text file in 'Program arguments' - Run 
* __Run using spark submit__: Go to 'gs-spark-java' project directory, build project using maven (i.e. `mvn clean package`) and submit to local spark cluster: 

    `spark-submit --master local[*] --class com.tirthal.learning.example.WordCountWithSparkRdd --packages org.apache.commons:commons-csv:1.2 target/gs-spark-java-1.0-SNAPSHOT.jar <path-of-text-file>`

    Note: The "--packages" parameter is used to include dependency (e.g. common csv) as a runtime dependency. Remember that there is no need to include Spark itself as a dependency since it is implied by default. More than one dependencies can be added as a comma-separated list of Maven IDs.
