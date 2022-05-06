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
import org.apache.spark.streaming.scheduler.{StreamingListener, StreamingListenerBatchCompleted}

import java.sql.Timestamp
import java.time.Instant

object SQLi {

  def main(args: Array[String]) {

    //SMS Setup
    val client = VonageClient.builder.apiKey("d05eb426").apiSecret("zBSv9seH5yDINPfu").build
    val phoneNumber = "66824345775";
    //Output Path from External Arg
    val outputPath = args(0)
    //Spark and Kafka Setup
    val conf = new SparkConf().setAppName("sqli-payload-detection")
    val ssc = new StreamingContext(conf, Seconds(30))

    val SQLi_payload_list: List[String] = List (" char( "," int( ","distinct","cast(","union","column","column_name",
      "table","table_name","table_schema","concat","convert","benchmark","count(","generate_series","information_schema",
      "schemata","login_test","tb_login","iif(","mid(","make_set","json_keys","elt(","sleep(",
      "procedure","analyse","extractvalue","MD5","null","%","(",")","\\","#","+", " or ", "||",
      "1=1"," AND "," OR ","- ","<",">","*");
    // ip:127.0.0.1, user-identifier:UD11, name:frank, time-stamp:[10/Oct/2000:13:55:36 -0700], header:"POST /?id=1' or '1' = '1'&password=message2 HTTP/1.0", status:200
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.148.0.5:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "get-post",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "maxRatePerPartition" -> new Integer(600000)
    )

    val topics = Array("get", "post")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    // Debug Print
    stream.map(record => (record.value().toString)).print

    //Seperate Lines
    val lines = stream.flatMap(_.value().split("\n"))

    //Template of Each Line
    // ip:127.0.0.1, user-identifier:UD11, name:frank, time-stamp:[10/Oct/2000:13:55:36 -0700], header:"POST /?id=1' or '1' = '1'&password=message2 HTTP/1.0", status:200

    lines.foreachRDD { rdd =>
      //Map Ip with Header
      val line = rdd.map(x => (x.split(", ")(0), x.split(", ")(4)))
      //Concat all headers with same IP
      val mapped = line.reduceByKey((x, y) => x+", "+y)
      //Filters header with signature collection
      val contains = mapped.filter(x => {SQLi_payload_list.exists(y => {x._2.contains(y)})})
      //Collection Step
      val finalRDD = contains.collect()
      ssc.addStreamingListener(new MyListener())
      //Print out IPs with headers that have signature overlap
      for (c <- finalRDD) {
        println(c._1 + " Suspicious Behavior [SQLi Attempt]" )

        val messageBody = c._1 + " Suspicious Behavior [SQLi Attempt] at " + Timestamp.from(Instant.now());
/*
        val message = new TextMessage("ProtocolIDS", phoneNumber, messageBody)

        val response = client.getSmsClient.submitMessage(message)

        if (response.getMessages.get(0).getStatus eq MessageStatus.OK) System.out.println("Message sent successfully.")

        else System.out.println("Message failed with error: " + response.getMessages.get(0).getErrorText)


*/
      }
      if (!contains.isEmpty()) {
        contains.saveAsTextFile(outputPath + "sqli-activity/" + Timestamp.from(Instant.now()).toString + "/")
      }
    }

    println("StreamingWordCount: streamingContext start")
    stream.context.start()
    println("StreamingWordCount: await termination")
    stream.context.awaitTermination()
    println("StreamingWordCount: done!")

    class MyListener() extends StreamingListener {
      override def onBatchCompleted(batchStarted: StreamingListenerBatchCompleted) {
        println("Total delay: " + batchStarted.batchInfo.totalDelay)
        println("Processing time : " + batchStarted.batchInfo.processingDelay)
        println("Scheduling Delay : " + batchStarted.batchInfo.schedulingDelay)
      }
    }
  }
}
