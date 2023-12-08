package com.mqtttrifork.consumer.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;


public class ConsumerMain {
  public static void main(String[] args) {
      ConsumerController cc = new ConsumerController();
      ConsumerRecords<String, String> records;
	  
	  cc.connectToDB("db");
      System.out.println("Connected to DB");
      
      cc.setUpConsumerKafka();
      System.out.println("Setted up Kafka consumer");
      
      cc.setUpProducerKafka();
      System.out.println("Setted up Kafka producer"); 

      while (true) {
    	records = cc.consumeMessages();
		for (ConsumerRecord<String, String> record : records) {
		    String message = record.value();
		    long timestamp = cc.extractTimestamp(message);
		    if (cc.isOneMinuteOld(timestamp)) {
		        // Discard the message
		    } else {
		        if (cc.isOdd(timestamp)) {
		            cc.rePublish();
		        } else {
		            cc.saveMessage();
		        }
		    }
		}
    }
  }
}