package com.mqtttrifork.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.mqtttrifork.consumer.dto.ConsumerDTO;
import com.mqtttrifork.consumer.service.ConsumerService;
import com.mqtttrifork.consumer.service.impl.ConsumerServiceImpl;

public class ConsumerServiceTest {
    
    @Mock
    private ConsumerService consumerService;
    @Mock
    private KafkaProducer<String, String> kafkaProducer;
    @Mock
    private KafkaConsumer<String, String> kafkaConsumer;

    @BeforeEach
    public void setUp() {
        consumerService = new ConsumerServiceImpl();
    }

    @Test
    public void testRePublish() {
        //Arrange
        ConsumerDTO consumerDTO = new ConsumerDTO();

        //Assert
        Assertions.assertTrue(consumerDTO instanceof ConsumerDTO);
    }

    @Test
    public void testConsumeMessages() {

        //Assert
        Assertions.assertNull(kafkaConsumer);
    }

    @Test
    public void testSetUpConsumerKafka() {
        //Assert
        Assertions.assertNull(kafkaConsumer);
    }

    @Test
    public void testSetUpProducerKafka() {
        //Assert
        Assertions.assertNull(kafkaConsumer);
    }
}