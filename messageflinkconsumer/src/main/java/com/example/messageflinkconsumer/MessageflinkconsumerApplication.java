package com.example.messageflinkconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microsoft.applicationinsights.attach.ApplicationInsights;

@SpringBootApplication
public class MessageflinkconsumerApplication {

	public static void main(String[] args) {
		ApplicationInsights.attach();
		SpringApplication.run(MessageflinkconsumerApplication.class, args);
	}

}
