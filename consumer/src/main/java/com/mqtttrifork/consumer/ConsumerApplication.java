package com.mqtttrifork.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class ConsumerApplication{

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
