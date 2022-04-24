import org.apache.spark.sql.functions.{explode, split}

// Kafka connection Setup
val kafka = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "HOST1:PORT1,HOST2:PORT2")   // List of broker:host
  .option("subscribe", "TOPIC1,TOPIC2")    // comma separated list of topics
  .option("startingOffsets", "latest") // read data from the end of the stream
  .load()

// In our case , we are expecting just text data which we will updates
// to perform the Famous Word-count use case
// "value" ---> Refers to Value in (key,value) pair
// split lines by space and explode the array .The column name will be `word`
val df = kafka.select(explode(split($"value".cast("string"), "\\s+")).as("word"))
  .groupBy($"word")
  .count


  // follow the word counts as it updates
display(df.select($"word", $"count"))