import sys
from pyspark.sql import SparkSession
from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext
from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split

if __name__ == "__main__":
    # create Spark context with necessary configuration
    spark = SparkSession \
        .builder \
        .appName("StructuredNetworkWordCount") \
        .getOrCreate()

    # Create DataFrame representing the stream of input lines from connection to localhost:9999
    lines = spark \
        .readStream \
        .format("socket") \
        .option("host", "localhost") \
        .option("port", 5000) \
        .load()

    # Split the lines into words
    words = lines.select(
        explode(
            split(lines.value, " ")
        ).alias("word")
    )

    # Generate running word count
    wordCounts = words.groupBy("word").count()
    query = wordCounts \
        .writeStream \
        .outputMode("complete") \
        .format("console") \
        .start()

    query.awaitTermination()

