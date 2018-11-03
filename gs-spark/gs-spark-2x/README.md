# Getting Started with Spark 2

Sample code to play with Spark 2 using Java, Python and Scala

## Software Installations and Setup Prerequisites

* Install Java 8
* Install Scala
* Install IPython (Anaconda with Python 3.6 version) and configure necessary environment variables for IPython (e.g. PYSPARK_DRIVER_PYTHON="ipython" and PYSPARK_DRIVER_PYTHON_OPTS="notebook")
* Setup Spark (e.g. spark-2.3.1-bin-hadoop2.7) and configure necessary environment variables (e.g. 'SPARK_HOME' and include '%SPARK_HOME%/bin' in path) 
* Setup Hadoop winutils in case of Window OS (e.g. hadoop-2.7.1-winutils) and configure related environment variable (e.g. 'HADOOP_HOME' and include '%HADOOP_HOME%/bin' in 'path')
* Install Intellij (preferably with Scala and Python Plugins) and open the 'gs-spark-2x' project in IDE

## Getting Started with Spark 2 using Java 

##### Understanding Spark RDD (i.e. create RDD from input text file, do transformations and perform actions) and Run a word count Spark Job
* See example java code of [Word count using Spark RDD](src/main/java/examples/WordCountSparkJavaExample.java)
* Run using Intellij locally: Select 'WordCountSparkJavaExample.java' - Press Alt+Shift+F10 - Edit Configuration - Configuration - Give location of any text file in 'Program arguments' - Run 
* Run using spark submit: Go to 'gs-spark-2x' project directory, build project using maven (i.e. `mvn install`) and submit to local spark cluster: `spark-submit --master local[*] --class examples.WordCountSparkJavaExample target/gs-spark-2x-1.0-SNAPSHOT.jar <path-of-text-file>`

## Getting Started with Spark 2 using Python 

##### Prerequisites
1. Open 'Anaconda Prompt'
2. Go to 'gs-spark-2x\src\main\python' directory of the project 
3. Run spark on local machine in standalone mode: `pyspark --master local[*]`

##### Understanding Spark RDD for data exploration, preparation and analysis i.e. cleaning, transforming and summarizing data
* [Spark RDD - Loading a data set and getting started with Spark's RDD](http://localhost:8888/notebooks/01/Spark-HelloWorld.ipynb) - a simple example of Spark RDD
* [Transforming and Cleaning Unstructured Data with Spark's RDD](http://localhost:8888/notebooks/02/NYCrimeAnalysis.ipynb) - learn Spark RDD's operations like filter, map, reduce, countByValue, etc.
* [Summarizing Data along Dimensions using Spark's PairRDD](http://localhost:8888/notebooks/03/DodgersSummary.ipynb) -  learn Spark PairRDD's operations like map, reduceByKey, sortBy, leftOuterJoin, combineByKey, etc.
* [Modeling Relationships to build co-occurrence Networks](http://localhost:8888/notebooks/04/MarvelRelationships.ipynb) - see usage of Spark RDD with power of Python 

##### Understanding Spark Streaming for processing large scale streaming data
* [Spark Streaming - Create DStream using socketTextStream and start listening for streaming data](http://localhost:8888/notebooks/05/Spark-Streaming-HelloWorld.ipynb)- a simple example of Spark Streaming 
* [Summarizing Data in entire Stream using updateStateByKey of DStream](http://localhost:8888/notebooks/06/Streaming-UpdateStateByKey.ipynb)- see usage of a stateful updateStateByKey operation of DStream
* [The countByWindow Transformation](http://localhost:8888/notebooks/07/Streaming-CountByWindow.ipynb), [The reduceByWindow Transformation](http://localhost:8888/notebooks/07/Streaming-ReduceByWindow.ipynb) and [The reduceByKeyAndWindow Transformation](http://localhost:8888/notebooks/Streaming-ReduceByKeyAndWindow.ipynb) to summarize data over a window - see usage of sliding window operations of DStream like countByWindow, reduceByWindow and reduceByKeyAndWindow stateful operations

## Getting Started with Spark 2 using Scala 

To be added...

# References

* [Spark courses on Pluralsight](https://www.pluralsight.com/search?q=spark&categories=course&sort=displayDate)
* [Spark Tutorials](https://techvidvan.com/tutorials/spark-tutorial/)
* Spark Examples [1](https://spark.apache.org/examples.html) | [2](https://github.com/apache/spark/tree/v2.3.1/examples)
* [How to use SparkSession in Apache Spark 2.0](https://databricks.com/blog/2016/08/15/how-to-use-sparksession-in-apache-spark-2-0.html)
* [How to submit Spark Applications](https://spark.apache.org/docs/2.3.1/submitting-applications.html)
* [Getting Started with Spark: Running a Simple Spark Job in Java](https://www.datasciencebytes.com/bytes/2016/04/18/getting-started-with-spark-running-a-simple-spark-job-in-java/)
* [Setup Development Environment for Intellij, Scala and Spark](https://kaizen.itversity.com/setup-development-environment-intellij-and-scala-big-data-hadoop-and-spark/)