# Protocol-IDS


In this project we try to combine Apache Kafka and Apache Spark Streaming to identify and detect common ways of exploiting web applications.


![System diagram_0](https://user-images.githubusercontent.com/79465272/168415121-ffc83590-5819-4afd-98eb-9c441918e2f7.png)


This project is Protocol-based IDS on Cloud Environment [GCP] using Apache Kafka & Spark Streaming.</br>
The Purpose is to detect Brute force attacks, DDoS attacks, SQL injection, and Cross-Site Scripting.</br>
Each detected intrusion is logged into the protocol-ids-output bucket according to timestamp, and SMS notifications will also be sent to telephone numbers that have been set using the Vonage SMS API.</br>
</br>

The Source Code for each Spark Job is written in Scala and can be modified in the corresponding Scala Folders in the Spark Jobs Folder.


Stack used:

1) Kafka
2) GCP Dataproc [Contains Spark]
3) Scala 
4) Python


## How to Start


###

Make sure to Start both VMs:
- KafkaVM
- Dataproc Master Node


### Start Kafka Environment

To access the first Terminal SSH to the Kafka VM under Compute Engine.

Terminal 1 
> cd /opt/kafka
> 
> sudo bin/zookeeper-server-start.sh config/zookeeper.properties

Terminal 2
> cd /opt/kafka
> 
> sudo bin/kafka-server-start.sh config/server.properties

### Start Producer (write)

For pasting the pseudo HTTP requests.

Terminal 3
> cd /opt/kafka
> 
> sudo bin/kafka-console-producer.sh --topic get --bootstrap-server localhost:9092

### Start Consumer (read)  

For debugging

Terminal 4
> cd /opt/kafka
>
> sudo bin/kafka-console-consumer.sh --topic get --from-beginning --bootstrap-server localhost:9092


#### Create New Topic 
> cd /opt/kafka
> 
> sudo bin/kafka-topics.sh --create --topic topic-name --bootstrap-server localhost:9092

#### Delete Topic
> cd /opt/kafka
> 
> sudo bin/kafka-topics.sh localhost:2181 --delete --topic topic-name --bootstrap-server localhost:9092


### Submit Spark Job

> gcloud dataproc jobs submit spark --jar=gs://protocol-ids-spark-jobs/job-name.jar --cluster=cluster-protocol-ids --properties=^#^spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2,com.vonage:client:6.2.0 --region=asia-southeast2 -- gs://protocol-ids-output/output-logs/



## Template for Logs

ip:127.0.0.1, user-identifier:UD11,  name:frank, time-stamp:[10/Oct/2000:13:55:36 -0700],  header:"GET /?id=message&password=message2 HTTP/1.0", status:200 

## References </br>
Wenyi Xu (2017) Visor: Real-time Log Monitor [Source Code and Log Pattern]. https://github.com/xuwenyihust/Visor</br>
payloadbox (2021) SQL Injection Payload List [Dataset]. https://github.com/payloadbox/sql-injection-payload-list</br>
payloadbox (2021) Cross Site Scripting  (XSS) Vulnerability Payload List [Dataset]. https://github.com/payloadbox/xss-payload-list</br>
iryndin (2018) 10K-Most-Popular-Passwords [Dataset]. https://github.com/iryndin/10K-Most-Popular-Passwords</br>
ferasalnaem (2021) sqli-detection-using-ML [ML Testing Source Code]. https://github.com/ferasalnaem/sqli-detection-using-ML


## Versions

  Scala 2.12.14
  Spark 3.1.2
