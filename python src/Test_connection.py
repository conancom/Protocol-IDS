from pyspark import SparkContext, SparkConf
from pyspark.sql import SQLContext

sc = SparkContext(conf=conf)
sqlContext=SQLContext(sc)

from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils

# Create streaming task
ssc = StreamingContext(sc, 0.10)
kafkaStream = KafkaUtils.createStream(ssc, "<HOSTNAMME:IP>", "spark-streaming-consumer", {'TOPIC1': 1})

query = kafkaStream \
        .writeStream \
        .outputMode("complete") \
        .format("console") \
        .start()

query.awaitTermination()