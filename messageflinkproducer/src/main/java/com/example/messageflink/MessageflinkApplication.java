package com.example.messageflink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microsoft.applicationinsights.attach.ApplicationInsights;

@SpringBootApplication
public class MessageflinkApplication {

	public static void main(String[] args) {
		ApplicationInsights.attach();
		SpringApplication.run(MessageflinkApplication.class, args);
	}

}
