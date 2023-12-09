package com.mqtttrifork.producer.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Controller
@Service
public class ProducerController {

    // Build to manage and keep track of 9,223,372,036,854,775,807 messages 
    private Map<String, Long> stats = new HashMap<>();
    private KafkaProducer<String, String> kafkaProducer;

    public ProducerController() {
        stats = new HashMap<>();
        stats.put("counter", (long) 0);
        stats.put("timestamp", (long) 0);

        // Initialize Kafka producer
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<>(properties);
    }

    public void publish() {
        // Generate timestamp and increment counter
        Long timestamp = System.currentTimeMillis();
        Long counter = this.stats.getOrDefault("counter", (long) 0) + 1;

        // Create message with timestamp and counter
        String message = "Timestamp: " + timestamp + ", Counter: " + counter;

        // Publish message to Kafka topic "trifork"
        kafkaProducer.send(new ProducerRecord<>("trifork", message));

        // Update stats hashmap
        this.stats.put("counter", counter);
        this.stats.put("timestamp", timestamp);
    }

    // REST endpoint to get the current status of the producer and the last message published
    @GetMapping("/")
    public ResponseEntity<String> getStatus() {
        // Generate timestamp and counter
        Long timestamp = this.stats.getOrDefault("timestamp", (long) 0);
        Long counter = this.stats.getOrDefault("counter", (long) 0);

        // Create message with timestamp and counter
        String message = "Timestamp: " + timestamp + ", Counter: " + counter;

        // Return the message as the response
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
   
    // Reset the counter and timestamp for testing purposes
    public void setStats(Map<String, Long> stats_test) {
        Long counter = stats_test.getOrDefault("counter", (long) 0);
        Long timestamp = stats_test.getOrDefault("timestamp", (long) 0);

        this.stats.put("timestamp", timestamp);
        this.stats.put("counter", counter);
    }
    
    // Get the current stats for testing purposes
    public Map<String, Long> getStats() {
        return this.stats;
    }

    
}