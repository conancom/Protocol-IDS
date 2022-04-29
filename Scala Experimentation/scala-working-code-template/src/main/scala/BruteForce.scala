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


object SQLi {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("sqli-payload-detection")
    val ssc = new StreamingContext(conf, Seconds(10))
    val SQLi_payload_list: List[String] = List ("char","int","distinct","cast","union","column","column_name",
      "table","table_name","table_schema","concat","convert","benchmark","count","generate_series","information_schema",
      "schemata","ctxsys","drithsx","sn","login_test","tb_login","iif","ord","mid","make_set","json_keys","elt","sleep",
      "procedure","analyse","extractvalue","MD5","null","WMlF","axUU","pLQD","VzxF","COIj","hOre","BPiw","yFlw","JVvY",
      "dZkl","RjPx","CENp","xwDm","vjdV","cNbH","SFyR","aTVH","ZsKF","CyJp","rZMF","cUjj","EUxQ","LCju","kuta","XZeD",
      "hAqN","CpcV","PoLE","VFvf","Obus","ekYn","yJsI","DmJo","QRPk","hDNb","GArX","jTQx","gmvs","NZwC","JUku","UOXN",
      "CFqL","akSy","UGlQ","XEqz","Kflk","szIf","pCpU","wjwv","brAJ","kdZk","qovn","qajJ","Vdgm","dCgi","jSJQ","weOs",
      "FGfr","SVtI","putE","pPdn","cBRe","NClW","Cyed","WFDK","rsAn","ryFD","FUoB","xDQE","JEnX","nlZq","Grvx","rSth",
      "YFJq","bpul","oYfK","EcoY","MMjX","lPcI","tZjB","EKWp","SjoD","itwk","fRLP","dMoi","hqAi","Coax","uPqw","zvVc",
      "VuGr","RsCt","VBDT","QoSP","NKWH","pBVS","mMBU","fQZL","lLXh","kKQQ","%","=","(",")","\\","#","+", "or", "||",
      "1=1","AND","OR","'","-","<",">","*");


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

      val line = rdd.map(x => (x.split(" ")(0), x)).collect()

      for (c <- line) {
        println(c)
      }
    }

    println("StreamingWordCount: streamingContext start")
    stream.context.start()
    println("StreamingWordCount: await termination")
    stream.context.awaitTermination()
    println("StreamingWordCount: done!")


  }
}
