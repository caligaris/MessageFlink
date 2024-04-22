package com.example.messageflink.message;

import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Message {
    public Long id;
    public String message;
    public Integer partition=0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    public Instant timestamp;

    public Message() {
    }

    public Message(Long id, String message, Instant timestamp, Integer partition) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.partition = partition;
    }

    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", partition=" + partition +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message that = (Message) o;
        return message.equals(that.message) && id == that.id && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, message, id);
    }    
}
