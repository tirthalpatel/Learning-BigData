# Getting Started with Spark using Python and Jupyter Notebook

Sample code to play with Spark using Python

## Required Software and Setup

* Install IPython (Anaconda with Python 3.6 version) and configure necessary environment variables for IPython (e.g. PYSPARK_DRIVER_PYTHON="ipython" and PYSPARK_DRIVER_PYTHON_OPTS="notebook")
* Setup Spark (e.g. spark-2.3.1-bin-hadoop2.7) and configure necessary environment variables (e.g. 'SPARK_HOME' and include '%SPARK_HOME%/bin' in path)  - [see](https://had00ping.wordpress.com/2017/11/14/setting-intellij-for-pyspark/)
* Setup Hadoop winutils in case of Window OS (e.g. hadoop-2.7.1-winutils) and configure related environment variable (e.g. 'HADOOP_HOME' and include '%HADOOP_HOME%/bin' in 'path')
* Install Intellij 2018.3+ (preferably with Python Plugin) and open the 'gs-spark-python' project in IDE
    
## Getting Started with Spark using Python and Jupyter Notebook

1. Open 'Anaconda Prompt'
2. Go to 'gs-spark-python\notebooks' directory of the project 
3. Run spark on local machine in standalone mode: `pyspark --master local[*]`
4. Open jupyter notebook UI in the browser: `http://localhost:8888/tree`

### Understanding Spark 2 programming concepts
* [Spark 2 Concepts](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/00/Spark2-Concepts.ipynb)

### Understanding Spark RDD for data exploration, preparation and analysis i.e. cleaning, transforming and summarizing data
* [Spark RDD - Loading a data set and getting started with Spark's RDD](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/01/Spark-HelloWorld.ipynb)
* [Transforming and Cleaning Unstructured Data with Spark's RDD](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/02/NYCrimeAnalysis.ipynb)
* [Summarizing Data along Dimensions using Spark's PairRDD](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/03/DodgersSummary.ipynb)
* [Modeling Relationships to build co-occurrence Networks](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/04/MarvelRelationships.ipynb)

### Understanding Spark Streaming for processing large scale streaming data
* [Spark Streaming - Create DStream using socketTextStream and start listening for streaming data](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/21/Spark-Streaming-HelloWorld.ipynb)
* [Summarizing Data in entire Stream using updateStateByKey of DStream](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/22/Streaming-UpdateStateByKey.ipynb)
* [The countByWindow Transformation](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/23/Streaming-CountByWindow.ipynb), [The reduceByWindow Transformation](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/23/Streaming-ReduceByWindow.ipynb) and [The reduceByKeyAndWindow Transformation](https://github.com/tirthalpatel/Learning-BigData/blob/master/gs-spark/gs-spark-python/notebooks/23/Streaming-ReduceByKeyAndWindow.ipynb) to summarize data over a window

