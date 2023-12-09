package com.mqtttrifork.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mqtttrifork.consumer.dto.ConsumerDTO;
import com.mqtttrifork.consumer.service.ConsumerService;
import com.mqtttrifork.consumer.service.impl.ConsumerServiceImpl;

public class ConsumerServiceTest {

    private ConsumerService consumerService;
    private KafkaProducer<String, String> kafkaProducer;
    private KafkaConsumer<String, String> kafkaConsumer;

    @BeforeEach
    public void setUp() {
        consumerService = new ConsumerServiceImpl();
        kafkaProducer = consumerService.setUpProducerKafka();
        kafkaConsumer = consumerService.setUpConsumerKafka(); 
    }

    @Test
    public void testRePublish() {
        //Arrange
        ConsumerDTO consumerDTO = new ConsumerDTO();

        //Act & Assert
        assertDoesNotThrow(() -> consumerService.rePublish(kafkaProducer, consumerDTO));
    }

    @Test
    public void testConsumeMessages() {
        //Act
        ConsumerRecords<String, String> consumerRecords = consumerService.consumeMessages(kafkaConsumer);

        //Assert
        Assertions.assertNotNull(consumerRecords);
        Assertions.assertSame(ConsumerRecords.class, consumerRecords.getClass());
    }

    @Test
    public void testSetUpConsumerKafka() {
        //Act
        KafkaConsumer<String, String> consumer = consumerService.setUpConsumerKafka();

        //Assert
        Assertions.assertNotNull(consumer);
        Assertions.assertSame(KafkaConsumer.class, consumer.getClass());
    }

    @Test
    public void testSetUpProducerKafka() {
        //Act
        KafkaProducer<String, String> producer = consumerService.setUpProducerKafka();

        //Assert
        Assertions.assertNotNull(producer);
        Assertions.assertSame(KafkaProducer.class, producer.getClass());
    }
}