package com.example.messageflinkconsumer.message;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MessageController {
    
    @GetMapping("/")
    public String getMethodName() {
        // return with status 200 OK
        return "OK";
    }
    
}
