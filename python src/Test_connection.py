import pyspark
from pyspark import SQLContext
from pyspark.shell import spark
from pyspark.sql import SparkSession

sc = SparkSession \
        .builder \
        .master('10.184.0.5:7077') \
        .appName("sparkFromJupyter") \
        .getOrCreate()
sqlContext = SQLContext(sparkContext=sc.sparkContext, sparkSession=sc)
print("Spark Version: " + sc.version)
print("PySpark Version: " + pyspark.__version__)



df = spark \
  .readStream \
  .format("kafka") \
  .option("kafka.bootstrap.servers", "10.184.0.3:9092") \
  .option("subscribe", "get") \
  .load()


df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
df.show()