package com.mqtttrifork.consumer.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsumerMain implements CommandLineRunner  {

	// Run the application in Spring Boot environment
    @Override
    public void run(String... args) throws Exception {
        main(args);
    }

	// Automatically inject the controller
	@Autowired
	private ConsumerController consumerController;
	
	public void main(String[] args) {
		ConsumerRecords<String, String> records;
		
		consumerController.connectToDB("db"); // "localhost:5432" for local testing or "db" production
		System.out.println("Connected to DB"); 
		
		consumerController.setUpConsumerKafka();
		System.out.println("Setted up Kafka consumer");
		
		consumerController.setUpProducerKafka();
		System.out.println("Setted up Kafka producer"); 

		while (true) {
			records = consumerController.consumeMessages();
			for (ConsumerRecord<String, String> record : records) {
				String message = record.value();
				long timestamp = consumerController.extractTimestamp(message);

				if (consumerController.isOneMinuteOld(timestamp)) {
					// Discard the message
				} else {
					if (consumerController.isOdd(timestamp)) {
						consumerController.rePublish();
					} else {
						consumerController.saveMessage();
					}
				}
			}
		}
    }
}