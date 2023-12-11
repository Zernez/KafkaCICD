package com.mqtttrifork.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import com.mqtttrifork.consumer.dto.ConsumerDTO;
import com.mqtttrifork.consumer.service.ConsumerService;
import com.mqtttrifork.consumer.service.impl.ConsumerServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@Service
public class ConsumerController {
	
	// Connector access to model level services and DTO
	@Autowired
	private ConsumerService consumerService;

	private Connection connDB;	
    private KafkaProducer<String, String> kafkaProducer;
    private KafkaConsumer<String, String> kafkaConsumer;
    private ConsumerDTO consumerDTO;
    
    // Initialize objects
    public ConsumerController() {
    	this.connDB = null;
    	this.kafkaProducer = null;
    	this.kafkaConsumer = null;
    	this.consumerDTO = new ConsumerDTO();
        this.consumerService = new ConsumerServiceImpl();
    }
    
    // Setup consumer Kafka and store the instance
    public void setUpConsumerKafka(){
    	this.kafkaConsumer = consumerService.setUpConsumerKafka();	
    }
    
    // Setup produce Kafka and store the instance    
    public void setUpProducerKafka(){
    	this.kafkaProducer = consumerService.setUpProducerKafka();    	
    }
    
    // Consume a message from the queue
    public ConsumerRecords<String, String> consumeMessages() {
            ConsumerRecords<String, String> records = consumerService.consumeMessages(this.kafkaConsumer);
            return records;
    }

    // Reinsert a message in the queue
    public void rePublish() {
    	consumerService.rePublish(this.kafkaProducer, this.consumerDTO);
    }

    // Save the message to a DB
	public void saveMessage() {
		try {
			consumerService.saveMessage(this.connDB, this.consumerDTO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    // Timestamps and the message are saved in DTO returning only the timestamp
    public long extractTimestamp(String message) {
    	long timestamp = consumerDTO.extractTimestamp(message);
        return timestamp;
    }

    // Check if the message is old more than one minute
    public boolean isOneMinuteOld(long timestamp) {
    	boolean result = consumerDTO.isOneMinuteOld(timestamp);
        return result;
    }

    // Check if the timestamp is odd
    public boolean isOdd(long number) {
    	boolean result = consumerDTO.isOdd(number);
        return result;
    }

    // Connect to the database and save the connection instance
	public void connectToDB(String host) {
		//Try to estabilish a connection in the service layer as singleton
		if (this.connDB == null){
			try {
				this.connDB = consumerService.connectToDB(host);
			} catch (SQLException e) {		
				e.printStackTrace();
			}		
		}
	}

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getStatus() {
        String dbMessages = null;
        String lastMessage = "Counter: " + String.valueOf(consumerDTO.getMessage()) + ", Timestamp: " + String.valueOf(consumerDTO.getTimestamp());
        String lastMessageToDB = "Counter: " + consumerDTO.getMessageToDB() + ", Timestamp: " + consumerDTO.getTimestampToDB();
        String lastMessageToResend = "Counter: " + consumerDTO.getMessageToResend() + ", Timestamp: " + consumerDTO.getTimestampToResend();

        try {
            dbMessages = consumerService.getLastThreeMessages(this.connDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load the HTML template from the static directory
        String htmlTemplate = loadHtmlTemplate();

        // Replace placeholders in the HTML template with the database messages and last message information
        String htmlResponse = htmlTemplate.replace("{{dbMessages}}", dbMessages)
                                          .replace("{{lastMessage}}", lastMessage)
                                          .replace("{{lastMessageToDB}}", lastMessageToDB)
                                          .replace("{{lastMessageToResend}}", lastMessageToResend);

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
}