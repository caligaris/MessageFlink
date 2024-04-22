package com.example.messageflinkconsumer.flink;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Consumer implements CommandLineRunner {
    @Value("${kafka.topic}")
    private String TOPIC;

    @Value("${bootstrap.servers:null}")
    private String BOOTSTRAP_SERVERS;
    
    @Value("${kafka.topic.connectionstring:null}")
    private String TOPIC_CONNECTION_STRING;

    private static final String FILE = "/consumer.config";

    @Override
    public void run(String... args)
        {
        try {
            //Load properties from config file
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
            
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            DataStream stream = env.addSource(new FlinkKafkaConsumer011(TOPIC, new SimpleStringSchema(), properties));
            stream.print();
            env.execute("Testing flink consumer");

        } catch(FileNotFoundException e){
            System.out.println("FileNoteFoundException: " + e);
        } catch (Exception e){
            System.out.println("Failed with exception " + e);
        }        
    }
    
}
