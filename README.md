# Protocol-IDS


In this project we try to combine Apache Kafka and Apache Spark Streaming to identify common ways of exploiting web applications.


Stack used:

1) Kafka
2) GCP Dataproc [Contains Spark]
3) Scala 
4) Java



## Commands

gcloud dataproc jobs submit spark --jar=gs://example-bucket-visor/working_print.jar --cluster=cluster-protocol-ids --properties spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2    --region=asia-southeast2 jobs submit spark  --jar=gs://example-bucket-visor/testingsbt_2.12-0.1.0-SNAPSHOT.jar     --cluster=cluster-81be  --properties spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2    --region=asia-southeast2

gcloud dataproc jobs submit pyspark gs://example-bucket-visor/working_print.jar --cluster=cluster-protocol-ids --properties spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2    --region=asia-southeast2


NEW: Socket Version

gcloud dataproc jobs submit spark --jar=gs://example-bucket-visor/socket_test.jar --cluster=cluster-protocol-ids --properties spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.2    --region=asia-southeast2 

gcloud dataproc jobs submit pyspark gs://example-bucket-visor/Test_connection_2.py --cluster=cluster-protocol-ids --region=asia-southeast2


Python 2.4;
gcloud dataproc jobs submit pyspark gs://example-bucket-visor/SQLi.py --cluster=cluster-for-pyspark --properties spark.jars.packages=org.apache.spark:spark-streaming-kafka-0-8_2.11:2.4    --region=asia-southeast2



Without Added Libray:

gcloud dataproc jobs submit spark  --jar=gs://example-bucket-visor/testingsbt_2.12-0.1.0-SNAPSHOT.jar     --cluster=cluster-81be  --region=asia-southeast2

## Template for Logs:

### NEW
ip:127.0.0.1, user-identifier:UD11, name:frank, time-stamp:[10/Oct/2000:13:55:36 -0700], header:"POST /?id=1' or '1' = '1'&password=message2 HTTP/1.0", status:200

GET) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"GET /apache_pb.gif HTTP/1.0" 200 2326

GET for SQLi) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"GET /?id=message&password=message2 HTTP/1.0" 200 2326

POST) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"POST /apache_pb.gif HTTP/1.0" 200 2326

POST for SQLi) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"POST id=message&password=message2 HTTP/1.0" 200 2326

PUT) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"PUT /apache_pb.gif HTTP/1.0" 200 2326

PUT for SQLi) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"PUT id=message&password=message2 HTTP/1.0" 200 2326

DELETE) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"DELETE /apache_pb.gif HTTP/1.0" 200 2326

DELETE for SQLi) 
ip:127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] header:"DELETE id=message&id=message2&password=message3 HTTP/1.0" 200 2326

AI:
gcloud ai-platform local predict --model-dir gs://model-randome-forest-sqli/ --framework  scikit-learn --json-instances gs://model-randome-forest-sqli/result.json

Version: 

  Scala 2.12.14
  Spark 3.1.2
