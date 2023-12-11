package com.mqtttrifork.consumer.service.impl;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.mqtttrifork.consumer.dto.ConsumerDTO;
import com.mqtttrifork.consumer.service.ConsumerService;
import java.sql.ResultSet;

//Makes the instance of a class in singleton
@Service
public class ConsumerServiceImpl implements ConsumerService{
	 
	  // Factory or Prototype OOP pattern as multiple implementation of the same interface
	  // Save the message into a DB forming a SQL query or update if the timestamp is the same
	  @Override
	  public ConsumerDTO saveMessage (Connection dbConn, ConsumerDTO consumerDTO) throws SQLException {
		  long timestamp = consumerDTO.getTimestamp();
		  long message = consumerDTO.getMessage();

		  consumerDTO.setTimestampToDB(String.valueOf(timestamp));
		  consumerDTO.setMessageToDB(String.valueOf(message));
		  
		    PreparedStatement insert = dbConn.prepareStatement(
				      "INSERT INTO messages (timestamp, message) VALUES (?, ?)");
				    insert.setLong(1, timestamp);
				    insert.setLong(2, message);
	
				    try {
				      insert.executeUpdate();
				    } catch (SQLException e) {
				      PreparedStatement update = dbConn.prepareStatement(
				        "UPDATE messages SET message = ? WHERE timestamp = ?");
				      update.setLong(1, message);
				      update.setLong(2, timestamp);
				      update.executeUpdate();
				    }
				    
	   return consumerDTO;		  
	  }
	  
	@Override
	public String getLastThreeMessages(Connection dbConn) throws SQLException {
		StringBuilder result = new StringBuilder();

		PreparedStatement select = dbConn.prepareStatement(
			"SELECT timestamp, message FROM messages ORDER BY timestamp DESC LIMIT 3");
		ResultSet resultSet = select.executeQuery();

		while (resultSet.next()) {
			long timestamp = resultSet.getLong("timestamp");
			long message = resultSet.getLong("message");

			result.append("Timestamp: ").append(timestamp).append(", Message: ").append(message).append("\n");
		}
		
		return result.toString();
	}
	  
	  // Connect to DB and return the connection object	  
	  @Override
	  public Connection connectToDB(String host) throws SQLException {
		    Connection conn = null;
	
		    try {
		      // Set up the driver and address
		      Class.forName("org.postgresql.Driver");
		      String url = "jdbc:postgresql://" + host + "/postgres";
	
		      while (conn == null) {
		        try {
		        	// Try to establish a connection and receive the object
		        	conn = DriverManager.getConnection(url, "postgres", "");
		        } catch (SQLException e) {
		        	System.err.println("Waiting for db");
		        	try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						System.exit(1);
					}
		        }
	      }
		  
		  // Create a table with timestamp as bigint key and bigint message
	      PreparedStatement st = conn.prepareStatement(
	        "CREATE TABLE IF NOT EXISTS messages (timestamp BIGINT NOT NULL UNIQUE, message BIGINT NOT NULL)");
	      st.executeUpdate();
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	      System.exit(1);
	    }
	   
	   return conn;
	  }
	
	// Reinsert the message in the queue with new timestamp and counter
	@Override
	public void rePublish(KafkaProducer<String, String> kafkaProducer, ConsumerDTO consumerDTO) {
	    // Generate timestamp and increment counter
	    long timestamp = System.currentTimeMillis();
	    consumerDTO.setTimestamp(timestamp);
	    long counter = 	consumerDTO.getMessage() + 1;
	    consumerDTO.setMessage(counter);
	
		consumerDTO.setTimestampToResend(String.valueOf(timestamp));
		consumerDTO.setMessageToResend(String.valueOf(counter));

	    // Create message with timestamp and counter
	    String message = "Timestamp: " + timestamp + ", Counter: " + counter;
	
	    // Publish message to Kafka topic "trifork"
	    kafkaProducer.send(new ProducerRecord<>("trifork", message));
	}
	
	// Take the message from the queue
	@Override
    public ConsumerRecords<String, String> consumeMessages(KafkaConsumer<String, String> kafkaConsumer) {
    	ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
        
    	return records;
    }
 
	// Setup consumer Kafka
	@Override
	public KafkaConsumer<String, String> setUpConsumerKafka(){
        // Initialize Kafka consumer
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka:9092");
        properties.put("group.id", "my-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("trifork"));
        
        return kafkaConsumer;
	}

	// Setup producer Kafka
	@Override
	public KafkaProducer<String, String> setUpProducerKafka(){
        // Initialize Kafka producer
		Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        
        return kafkaProducer;
	}
}
