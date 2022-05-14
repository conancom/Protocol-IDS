import sys
from pyspark.sql import SparkSession
from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext
from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split

if __name__ == "__main__":

    batchIntervalSeconds = 10
    sc = SparkContext(appName='TestConnection')
    ssc = StreamingContext(sc, batchIntervalSeconds)
    # Set each DStreams in this context to remember RDDs it generated in the last given duration.
    # DStreams remember RDDs only for a limited duration of time and releases them for garbage
    # collection. This method allows the developer to specify how long to remember the RDDs (
    # if the developer wishes to query old data outside the DStream computation).

    lines = ssc.socketTextStream('0.0.0.0', 9999)
    lines.pprint()

    words = lines.flatMap(lambda line: line.split(","))
    pairs = words.map(lambda word: (word, 1))
    wordCounts = pairs.reduceByKey(lambda x, y: x + y)
    # This line starts the streaming context in the background.
    ssc.start()

    # This ensures the cell is put on hold until the background streaming thread is started properly.
    ssc.awaitTermination();


