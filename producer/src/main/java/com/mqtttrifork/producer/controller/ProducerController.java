package com.mqtttrifork.producer.controller;


import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Service
public class ProducerController {

    // Build to manage and keep track of 9,223,372,036,854,775,807 messages 
    private Map<String, Long> stats = new HashMap<>();
    private KafkaProducer<String, String> kafkaProducer;
    private Properties properties;

    public ProducerController() {
        this.stats = new HashMap<>();
        this.stats.put("counter", (long) 0);
        this.stats.put("timestamp", (long) 0);

        // Initialize Kafka producer
        this.properties = new Properties();
        this.properties.put("bootstrap.servers", "localhost:9092");
        this.properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<>(this.properties);
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

    // Return the HTML page with the current stats
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getStatus() {
        // Generate timestamp and counter
        Long timestamp = this.stats.getOrDefault("timestamp", (long) 0);
        Long counter = this.stats.getOrDefault("counter", (long) 0);

        // Load the HTML template from the static directory
        String htmlTemplate = loadHtmlTemplate();

        // Replace placeholders in the HTML template with the timestamp and counter
        String htmlResponse = htmlTemplate.replace("{{timestamp}}", timestamp.toString())
                                          .replace("{{counter}}", counter.toString());

        // Return the HTML page as the response
        return htmlResponse;
    }

    // Load the HTML template from the static directory
    private String loadHtmlTemplate() {
        try {
            // Load the HTML template file from the static directory
            Resource resource = new ClassPathResource("static/index.html");
            byte[] htmlBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            return new String(htmlBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Handle the exception if the HTML template file cannot be loaded
            e.printStackTrace();
            return "";
        }
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