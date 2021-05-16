# Getting Started with Spark using Python and Jupyter Notebook

## Required Software and Setup

* Install IPython (i.e. Anaconda with Python 3.8 or latest version)
* Setup Spark (e.g. spark-3.3.1-bin-hadoop3.2) and configure necessary environment variables (e.g. 'SPARK_HOME' and include '%SPARK_HOME%/bin' in path)  - [see](https://had00ping.wordpress.com/2017/11/14/setting-intellij-for-pyspark/)
* In case of Window OS, additionally setup Hadoop winutils and configure related environment variable (e.g. 'HADOOP_HOME' and include '%HADOOP_HOME%/bin' in 'path')

## Prerequisite: Run Jupyter Notebook to play with *.ipynb code

1. Open 'Anaconda Prompt / Terminal'
2. Go to 'gs-spark-python\notebooks' directory of the project 
3. Run spark on local machine in standalone mode (using PYSPARK_DRIVER_PYTHON and PYSPARK_DRIVER_PYTHON_OPTS to integrate Jupyter Notebook with PySpark): `PYSPARK_DRIVER_PYTHON="jupyter" PYSPARK_DRIVER_PYTHON_OPTS="notebook" pyspark --master local[*]`
4. Open jupyter notebook UI in the browser: `http://localhost:8888/tree`

## Understanding Spark 2 programming concepts

* [Spark 2 Concepts](http://localhost:8888/notebooks/00/Spark2-Concepts.ipynb) - understand SparkContext, SparkSession, RDD, Dataset, DataFrame and SQLContext

## Understanding Spark RDD for data exploration, preparation and analysis i.e. cleaning, transforming and summarizing data

* [Spark RDD - Loading a data set and getting started with Spark's RDD](http://localhost:8888/notebooks/01/SparkRdd-HelloWorld.ipynb) - a simple example of Spark RDD
* [Transforming and Cleaning Unstructured Data with Spark's RDD](http://localhost:8888/notebooks/02/SparkRdd-NYCrimeAnalysis.ipynb) - learn Spark RDD's operations like filter, map, reduce, countByValue, etc.
* [Summarizing Data along Dimensions using Spark's PairRDD](http://localhost:8888/notebooks/03/SparkRdd-DodgersSummary.ipynb) -  learn Spark PairRDD's operations like map, reduceByKey, sortBy, leftOuterJoin, combineByKey, etc.
* [Modeling Relationships to build co-occurrence Networks](http://localhost:8888/notebooks/04/SparkRdd-MarvelRelationships.ipynb) - see usage of Spark RDD with power of Python

## Understanding Spark DataFrames and Spark SQL for exploring, analyzing and querying data

* [Understanding Spark DataFrames and Spark SQL](http://localhost:8888/notebooks/11/Spark-DataFrame-Sql-Concepts.ipynb) - getting started code snippets of Spark DataFrames and Spark SQL
* [Exploring and Analyzing data with DataFrames](http://localhost:8888/notebooks/12/Spark-DataFrame-NYCrimeAnalysis.ipynb) - learn aggregation, grouping, sampling, ordering and joining data using Spark DataFrame + Using Broadcast Variables and Accumulators with DataFrame

## Understanding Spark Streaming (DStreams) concepts

* [Spark Streaming - Create DStream using socketTextStream and start listening for streaming data](http://localhost:8888/notebooks/21/Spark-Streaming-HelloWorld.ipynb)- a simple example of Spark Streaming
* [Summarizing Data in entire Stream using updateStateByKey of DStream](http://localhost:8888/notebooks/22/Streaming-UpdateStateByKey.ipynb)- see usage of a stateful updateStateByKey operation of DStream
* [The countByWindow](http://localhost:8888/notebooks/23/Streaming-CountByWindow.ipynb), [The reduceByWindow](http://localhost:8888/notebooks/23/Streaming-ReduceByWindow.ipynb) and [The reduceByKeyAndWindow](http://localhost:8888/notebooks/23/Streaming-ReduceByKeyAndWindow.ipynb) transformation to summarize data over a window - see usage of sliding window operations of DStream like countByWindow, reduceByWindow and reduceByKeyAndWindow stateful operations

