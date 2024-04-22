package com.example.messageflink.flink;

import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema.KafkaSinkContext;
// import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
// import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
// import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializationFeature;
// import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.JsonMapper;
// import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.example.messageflink.message.Message;

public class MessageSerializer implements KafkaRecordSerializationSchema<Message>{
    public static final long serialVersionUID = 1L;

    public String topic;
    public static final ObjectMapper objectMapper =
            JsonMapper.builder()
                    .build()
                    .registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public MessageSerializer() {}

    public MessageSerializer(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(
            Message element, KafkaSinkContext context, Long timestamp) {

        try {
            return new ProducerRecord<>(
                    topic,
                    element.partition,
                    element.timestamp.toEpochMilli(),
                    null,
                    objectMapper.writeValueAsBytes(element));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize record: " + element, e);
        }
    }    
}
