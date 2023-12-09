package com.mqtttrifork.consumer.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import com.mqtttrifork.consumer.dto.ConsumerDTO;

// Services offered by interface
public interface ConsumerService {

	ConsumerDTO saveMessage(Connection dbConn, ConsumerDTO consumerDTO) throws SQLException;
	
	Connection connectToDB(String host) throws SQLException;
	
	void rePublish(KafkaProducer<String, String> kafkaProducer, ConsumerDTO consumerDTO);
	
	ConsumerRecords<String, String> consumeMessages(KafkaConsumer<String, String> kafkaConsumer);
	
	KafkaConsumer<String, String> setUpConsumerKafka();
	
	KafkaProducer<String, String> setUpProducerKafka();

	String getLastThreeMessages(Connection dbConn) throws SQLException;
}




