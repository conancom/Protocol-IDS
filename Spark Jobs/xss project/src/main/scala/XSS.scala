import org.apache.spark.{SparkConf, SparkContext, rdd}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe


object XSS {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("xss-payload-detection")
    val ssc = new StreamingContext(conf, Seconds(10))
    val xss_payload_list: List[String] = List("<image", "<audio", "<video",
      "<body", "<object",  "<script",  "<svg",  "<title",  "<iframe",  "<frameset",
      "<html",  "<bgsound",  "<style",  "<applet",  "<marquee",  "<xml",  "--><!--",
      "<!--\\x3E<img",  "`/\"'><img",  "<a",  "<script>/*",  "\"'`>ABC<div",  "ABC<div",
      "<input",  "<form",  "<math",  "<table",  "<li", "<embed", "<b", "<div", "<x",
      "<!", "<?", "</", "<%", "<link", "<style>@import", "<//", "<meta",
      "<vmlframe", "<event-source", "<FRAMESET><FRAME", "<BR", "<LAYER", "<XSS",
      "<STYLE>li", "<!--[if", "<BASE", "<P", "<?xml", "<SCRIPT/XSS", "<form><textarea",
      "<var", "<TD", "&lt;DIV", "<;IMG", "<;SCRIPT", "<;BASE", "<;STYLE",
      "<;DIV", "<;HTML", "<;BODY", "<;BGSOUND", "<;IFRAME", "<;INPUT", "<;TABLE", "<;XML", "<;?",
      "<;BR", "<;XSS", "<;META", "<;A", "\"-prompt(8)-\"", "'-prompt(8)-'", "\";a=prompt,a()//",
      "';a=prompt,a()//", "'-eval", "\"-eval", "\"onclick=prompt(8)", "script>", "script%",
      "src=xxx:x", "&lt;", "XSS", "xss", "&#x", "(88,83,83)");

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

    // Print as Raw Input
    stream.map(record => (record.value().toString)).print
    // RDD[String] = ssc.sparkContext.emptyRDD[(String)]
    val lines = stream.flatMap(_.value().split(","))
    // ip:127.0.0.1
    // user-identifier:UD11
    // name:frank
    // time-stamp:[10/Oct/2000:13:55:36 -0700]
    // header:"POST /?id=1' or '1' = '1'&password=message2 HTTP/1.0"
    // status:200

    lines.foreachRDD { rdd =>
      val payload = rdd.filter(_.contains("header:"))

      var contains = payload.filter(_.contains("<img"))

      for (pl <- xss_payload_list){
        contains = payload.filter(_.contains(pl)).union(contains)
      }
      //val ip = contains.filter(x => SQLi_payload_list.contains(x))
      val collected = contains.map(record => (record, 1))
      val counts = collected.reduceByKey((x, y) => x + y).collect()
      //val collected = rdd.map(record => ( record.key(), record.value() )).collect()
      for (c <- counts) {
        println(c + "Is Query")

      }
    }

    println("StreamingWordCount: streamingContext start")
    stream.context.start()
    println("StreamingWordCount: await termination")
    stream.context.awaitTermination()
    println("StreamingWordCount: done!")


  }
}
