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
import java.sql.Timestamp
import java.time.Instant

object XSS{

  def main(args: Array[String]) {

    //SMS Setup
    val client = VonageClient.builder.apiKey("d05eb426").apiSecret("zBSv9seH5yDINPfu").build
    val phoneNumber = "66819851798";
    //Output Path from External Arg
    val outputPath = args(0)
    //Spark and Kafka Setup
    val conf = new SparkConf().setAppName("xss-payload-detection")
    val ssc = new StreamingContext(conf, Seconds(20))

    val xss_payload_list: List[String] = List("<img", "img>", "<image", "image>", "<audio", "audio>",
      "<video", "video>", "<body", "body>", "<object", "object>",  "<script", "script>", "<svg", "svg>",
      "<title", "title>", "<iframe", "iframe>", "<frameset", "frameset>", "<html", "html>", "<bgsound",
      "<style", "style>", "<applet", "applet>", "<marquee", "marquee>", "<xml", "xml>",  "--><!--",
      "<!--\\x3E<img",  "`/\"'><img", "<a", "</a", "<script>/*",  "\"'`>ABC<div",  "ABC<div",
      "<input", "input>", "<form", "form>", "<math", "math>", "<table", "table>", "<li", "li>", "<embed",
      "embed>", "<b", "<div", "div>", "<x",
      "<!", "<?", "</", "<%", "<link", "link>", "<style>@import", "<//", "<meta", "meta>",
      "<vmlframe", "<event-source", "<FRAMESET><FRAME", "<BR", "BR>", "<LAYER", "LAYER>", "<XSS", "XSS>",
      "<STYLE>li", "<!--[if", "<BASE", "<P", "<?xml", "<SCRIPT/XSS", "<form><textarea",
      "<var", "var>", "<TD", "TD>", "&lt;DIV", "<;IMG", "<;SCRIPT", "<;BASE", "<;STYLE",
      "<;DIV", "<;HTML", "<;BODY", "<;BGSOUND", "<;IFRAME", "<;INPUT", "<;TABLE", "<;XML", "<;?",
      "<;BR", "<;XSS", "<;META", "<;A", "\"-prompt(8)-\"", "'-prompt(8)-'", "\";a=prompt,a()//",
      "';a=prompt,a()//", "'-eval", "\"-eval", "\"onclick=prompt(8)", "script%",
      "src=xxx:x", "&lt;", "XSS", "xss", "&#x", "(88,83,83)", "<menu ", "menu>",
      "‘; alert(1);", "‘)alert(1);//", "<keygen ","keygen>", "alert(",
      "a=\\\"get\\\";", "b=\\\"URL(\\\\\"\\\";", "c=\\\"javascript&#058;\\\""
    )

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.148.0.5:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "get-post",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
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
      val contains = mapped.filter(x => {xss_payload_list.exists(y => {x._2.contains(y)})})
      //Collection Step
      val finalRDD = contains.collect()
      //Print out IPs with headers that have signature overlap
      for (c <- finalRDD) {
        println(c._1 + " Suspicious Behavior [XSS Attempt]")

        val messageBody = c._1 + " Suspicious Behavior [XSS Attempt] at " + Timestamp.from(Instant.now());

        val message = new TextMessage("ProtocolIDS", phoneNumber, messageBody)



        val response = client.getSmsClient.submitMessage(message)

        if (response.getMessages.get(0).getStatus eq MessageStatus.OK) System.out.println("Message sent successfully.")

        else System.out.println("Message failed with error: " + response.getMessages.get(0).getErrorText)




      }


      if (!contains.isEmpty()) {

        contains.saveAsTextFile(outputPath + "xss-activity/" + Timestamp.from(Instant.now()).toString + "/")
      }

    }

    println("StreamingWordCount: streamingContext start")
    stream.context.start()
    println("StreamingWordCount: await termination")
    stream.context.awaitTermination()
    println("StreamingWordCount: done!")

  }
}
