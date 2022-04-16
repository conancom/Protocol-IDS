import org.apache.spark.{SparkConf, SparkContext, rdd}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark
import org.apache.spark.sql.{SparkSession, Strategy}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.configuration.{Algo, QuantileStrategy, Strategy}
import org.apache.spark.mllib.tree.impurity.Entropy
import org.apache.spark.mllib.util.MLUtils

import scala.util.Random



object SQLi {


  def runpython(file:String)={

    Seq("python",file)
    Seq("python"
      ,"-c"
      ,"print('Hello from python in Scala');"
    )
  }


  def main(args: Array[String]) {

    println("Hello from Scala")

    val file="/Test.py"
    runpython(file)

/*
    val modelPath = "Random"


    val conf = new SparkConf().setAppName("WordCount")
    val ssc = new StreamingContext(conf, Seconds(10))


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.184.0.3:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "get",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("get")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    // Print as Raw Input
    stream.map(record=>(record.value().toString)).print

    val lines = stream.flatMap(_.value().split(" "))


    lines.foreachRDD { rdd =>
      //
      val ip = rdd.filter(_.contains("ip"))
      val collected = ip.map(record => (record, 1))
      val counts = collected.reduceByKey((x, y) => x + y).collect()
      //val collected = rdd.map(record => ( record.key(), record.value() )).collect()
      for (c <- counts) {
        println(c)
      }
    }

    println("StreamingWordCount: streamingContext start")
    stream.context.start()
    println("StreamingWordCount: await termination")
    stream.context.awaitTermination()
    println("StreamingWordCount: done!")
*/
  }
}