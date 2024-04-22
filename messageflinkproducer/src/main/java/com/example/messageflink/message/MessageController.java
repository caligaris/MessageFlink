package com.example.messageflink.message;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.messageflink.flink.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    @Autowired
    private producer producer;

    MessageController(producer producer) {
        this.producer = producer;
    }
    
    @RequestMapping()
    public Message getMessage() {
        return new Message(1L, "Hello from Flink!", java.time.Instant.now(), 0);
    }

    @PostMapping()
    public String postMethodName(@RequestBody Message entity) {
        //TODO: process POST request

        this.producer.send(entity);

        return entity.toString();
    }
    
}
