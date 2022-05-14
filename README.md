# Protocol-IDS


In this project we try to combine Apache Kafka and Apache Spark Streaming to identify and detect common ways of exploiting web applications.


Stack used:

1) Kafka
2) GCP Dataproc [Contains Spark]
3) Scala 
4) Python


## How to Start

### Start Kafka Environment

To access the first Terminal SSH to the Kafka VM under Compute Engine.

Terminal 1 
sudo bin/zookeeper-server-start.sh config/zookeeper.properties

Terminal 2
sudo bin/kafka-server-start.sh config/server.properties

### Start Producer (write)

For pasting the pseudo HTTP requests.

Terminal 3
sudo bin/kafka-console-producer.sh --topic get --bootstrap-server localhost:9092

### Start Consumer (read)  

For debugging

Terminal 4
sudo bin/kafka-console-consumer.sh --topic get --from-beginning --bootstrap-server localhost:9092


#### Create New Topic 

sudo bin/kafka-topics.sh --create --topic topic-name --bootstrap-server localhost:9092

#### Delete Topic
sudo bin/kafka-topics.sh localhost:2181 --delete --topic topic-name --bootstrap-server localhost:9092


### Submit Spark Job

gcloud dataproc jobs submit spark --jar=gs://protocol-ids-spark-jobs/job-name.jar --cluster=cluster-protocol-ids --properties=^#^spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2,com.vonage:client:6.2.0 --region=asia-southeast2 -- gs://protocol-ids-output/output-logs/



## Template for Logs:


Version: 

  Scala 2.12.14
  Spark 3.1.2
