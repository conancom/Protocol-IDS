# Protocol-IDS


In this project we try to combine Apache Kafka and Apache Spark Streaming to identify and detect common ways of exploiting web applications.


Stack used:

1) Kafka
2) GCP Dataproc [Contains Spark]
3) Scala 
4) Python


## Commands

gcloud dataproc jobs submit spark --jar=gs://protocol-ids-spark-jobs/xss_2.12-1.0.0.jar --cluster=cluster-protocol-ids --properties=^#^spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2,com.vonage:client:6.2.0 --region=asia-southeast2 -- gs://protocol-ids-output/output-logs/



## Template for Logs:


Version: 

  Scala 2.12.14
  Spark 3.1.2
