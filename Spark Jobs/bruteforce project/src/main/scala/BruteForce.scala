import org.apache.spark.SparkConf
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import com.vonage.client.VonageClient
import com.vonage.client.sms.MessageStatus
import com.vonage.client.sms.messages.TextMessage
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.scheduler.{StreamingListener, StreamingListenerBatchCompleted}

import java.sql.Timestamp
import java.time.Instant


object BruteForce {

  def main(args: Array[String]) {


    //SMS Setup
    val client = VonageClient.builder.apiKey("d05eb426").apiSecret("zBSv9seH5yDINPfu").build
    val phoneNumber = "66819851798";
    //Output Path from External Arg
    val outputPath = args(0)
    //Spark and Kafka Setup

    val timeInSeconds: Int = 30;
    val requestsPerSecUser: Int = 3;
    val requestsPerCurr: Int = requestsPerSecUser * timeInSeconds
    val conf = new SparkConf().setAppName("brute-force-detection")
    val ssc = new StreamingContext(conf, Seconds(timeInSeconds))


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.148.0.5:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "get",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "maxRatePerPartition" -> new Integer(600000)
    )

    val topics = Array("get")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    // Print as Raw Input
    stream.map(record=>(record.value().toString)).print
    //Split By comma
    val lines = stream.flatMap(_.value().split("\n"))

    lines.foreachRDD { rdd =>
      //Map each IP to 1
      val collected = rdd.map(record => (record.split(", ")(0), 1))
      //Reduce IPs to count each IP address's frequency
      val counts = collected.reduceByKey((x, y) => x + y)
      //Filter to take only Number of IPs in Threshold

      val countFinal = counts.filter(x => x._2>requestsPerCurr)

      val countCollected = countFinal.collect()

      ssc.addStreamingListener(new MyListener())
      //Print
      for (c <- countCollected) {
          println(c._1 + " Suspicious Behavior [Brute Force Attempt]" )

          val messageBody = c._1 + " Suspicious Behavior [Brute Force Attempt] at " + Timestamp.from(Instant.now());
    /*
          val message = new TextMessage("ProtocolIDS", phoneNumber, messageBody)

          val response = client.getSmsClient.submitMessage(message)

          if (response.getMessages.get(0).getStatus eq MessageStatus.OK) System.out.println("Message sent successfully.")

          else System.out.println("Message failed with error: " + response.getMessages.get(0).getErrorText)

*/
      }

      if (!countFinal.isEmpty()) {
        countFinal.saveAsTextFile(outputPath + "brute-force-activity/" + Timestamp.from(Instant.now()).toString + "/")
      }

    }

    println("StreamingWordCount: streamingContext start")
    stream.context.start()
    println("StreamingWordCount: await termination")
    stream.context.awaitTermination()
    println("StreamingWordCount: done!")

  }

  class MyListener() extends StreamingListener {
    override def onBatchCompleted(batchStarted: StreamingListenerBatchCompleted) {
      println("Total delay: " + batchStarted.batchInfo.totalDelay)
      println("Processing time : " + batchStarted.batchInfo.processingDelay)
      println("Scheduling Delay : " + batchStarted.batchInfo.schedulingDelay)
    }
  }
}
