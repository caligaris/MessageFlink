package com.example.messageflink.flink;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.messageflink.message.Message;

@Component
public class producer {
    @Value("${kafka.topic}")
    private String TOPIC;
    
    @Value("${bootstrap.servers:null}")
    private String BOOTSTRAP_SERVERS;
    
    @Value("${kafka.topic.connectionstring:null}")
    private String TOPIC_CONNECTION_STRING;
    
    private static final String FILE = "/producer.config";
    
    public void send(Message message) {

        try {

            // load properties from file
            Properties properties = new Properties();
            InputStream input = getClass().getResourceAsStream(FILE);
            properties.load(input);
    
            if (!BOOTSTRAP_SERVERS.equals("null")){
                properties.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS + ":9093");
            }
            if (!TOPIC_CONNECTION_STRING.equals("null")){
                properties.setProperty("sasl.jaas.config", 
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\" password=\"" + TOPIC_CONNECTION_STRING + "\";");
            }

            // partition set instantiation
            // final HashSet<TopicPartition> partitionSet = new HashSet<>(
            //     Arrays.asList(
            //         new TopicPartition(TOPIC, 0),
            //         new TopicPartition(TOPIC, 1)
            //     )   
            // );
    
            // set properties.
            KafkaSinkBuilder<Message> kafkaSinkBuilder = KafkaSink.<Message>builder();
            for(String property: properties.stringPropertyNames())
            {
                System.out.println(property + " : " + properties.getProperty(property));
                kafkaSinkBuilder.setProperty(property, properties.getProperty(property));
            }
    
            // set serializer type.
            // kafkaSinkBuilder.setRecordSerializer(KafkaRecordSerializationSchema.builder()
            // .setTopic(TOPIC)
            // .setValueSerializationSchema(new SimpleStringSchema())
            // .build());
            kafkaSinkBuilder.setRecordSerializer(new MessageSerializer(TOPIC));

            KafkaSink<Message> kafkaSink = kafkaSinkBuilder.build();
    
            // create stream environment, send simple stream to eventhub.
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            //DataStream<String> sourceStream = env.fromElements("1", "2", "3");
            message.timestamp = java.time.Instant.now();
            DataStream<Message> sourceStream = env.fromElements(message);
            sourceStream.sinkTo(kafkaSink);
    
            // run the job.
            env.execute("Send to eventhub using Kafka api");       
        }catch(FileNotFoundException e){
            System.out.println("FileNotFoundException: " + e);
        } catch (Exception e) {
            System.out.println("Failed with exception:: " + e);
        }
    } 
}
