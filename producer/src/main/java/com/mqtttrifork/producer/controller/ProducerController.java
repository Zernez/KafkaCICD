package com.mqtttrifork.producer.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@RestController
@RequestMapping("/")
public class ProducerController {

	// Build to manage and keep track of 9,223,372,036,854,775,807 messages 
    private Map<String, Long> stats = new HashMap<>();
    private KafkaProducer<String, String> kafkaProducer;

    public ProducerController() {
        // Initialize Kafka producer
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<>(properties);
    }

    public void publish() {
        // Generate timestamp and increment counter
        long timestamp = System.currentTimeMillis();
        long counter = stats.getOrDefault("counter", (long) 0) + 1;

        // Create message with timestamp and counter
        String message = "Timestamp: " + timestamp + ", Counter: " + counter;

        // Publish message to Kafka topic "trifork"
        kafkaProducer.send(new ProducerRecord<>("trifork", message));

        // Update stats hashmap
        stats.put("counter", counter);
        stats.put("nMessages", stats.getOrDefault("nMessages", (long) 0) + 1);
    }

    @GetMapping("/status")
    public ResponseEntity<Long> getStatus() {
    	// Give a response to a REST get request in number of messages
        return new ResponseEntity<>(stats.getOrDefault("nMessages", (long) 0), HttpStatus.OK);
    }

	public void setStats(Map<String, Long> stats_test) {
        
		// Update stats hashmap of the counter and number of messages
		long counter = stats_test.getOrDefault("counter", (long) 0);
		long nMessages = stats_test.getOrDefault("nMessages", (long) 0);		
		
        stats.put("counter", counter);
        stats.put("nMessages", nMessages);
	}
}