# import sparkpickle
# import joblib
# import pickle
# import base64
# from pyspark import SparkContext, SparkConf
from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils, TopicAndPartition

# from kafka import KafkaProducer


    import sys

    # import os

    # os.environ['PYSPARK_SUBMIT_ARGS'] = '--packages org.apache.spark:spark-streaming-kafka-0-8_2.11:2.4 pyspark-shell'
    batch_time = 10

    topics = ["get"]
    sc = SparkContext(appName="PythonSparkStreamingKafka", master="local[4]")
    ssc = StreamingContext(sc, batch_time)

    kafkaParams = {"metadata.broker.list": "10.148.0.5:9092"}
    start = 0
    partition = 0
    topic = 'get'

    topicPartion = TopicAndPartition(topic, partition)
    fromOffset = {topicPartion: int(start)}

    kafkaStream = KafkaUtils.createDirectStream(ssc, [topic], kafkaParams, fromOffsets=fromOffset)

    kafkaStream.pprint()

    ssc.start()
    ssc.awaitTermination()

# Load the model from the file
# randomForest_from_joblib = joblib.load('RandomForest.pkl')
# myvectorizer = pickle.load(open("myVectorizer", 'rb'))
'''

def clean_data(input_val):
    input_val = input_val.replace('\n', '')
    input_val = input_val.replace('%20', ' ')
    input_val = input_val.replace('=', ' = ')
    input_val = input_val.replace('((', ' (( ')
    input_val = input_val.replace('))', ' )) ')
    input_val = input_val.replace('(', ' ( ')
    input_val = input_val.replace(')', ' ) ')

    return input_val


def predict_sqli_attack():
    repeat = True

    space = ''
    for i in range(20):
        space += "-"

    print(space)
    input_val = input("Please enter some data: ")
    print(space)

    if input_val == '0':
        repeat = False

    input_val = clean_data(input_val)
    input_val = [input_val]

    input_val = myvectorizer.transform(input_val).toarray()
    result = randomForest_from_joblib.predict(input_val)

    print(space)

    if repeat == True:

        if result == 1:
            print("ALERT! This could be SQL injection attack!")


        elif result == 0:
            print("It seems to be a benign")

        print(space)

        predict_sqli_attack()

    elif repeat == False:
        print("closing ")

'''
