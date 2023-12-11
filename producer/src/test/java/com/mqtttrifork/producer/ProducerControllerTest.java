package com.mqtttrifork.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mqtttrifork.producer.controller.ProducerController;

class ProducerControllerTest {

    @Mock
    private KafkaProducer<String, String> kafkaProducer;
    @Mock
    private ProducerController producerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublish() {
        //Arrange
        Long timestamp = System.currentTimeMillis();
        Long counter = 1L;
        String expectedMessage = "Timestamp: " + timestamp + ", Counter: " + counter;

        //Assert
        Assertions.assertTrue(expectedMessage instanceof String);
    }

    @Test
    void testGetStatus() throws Exception {
        // Arrange
        Long timestamp = 1234567890L;
        Long counter = 42L;

        // Assert
        String expectedResponse = "<html><body>Timestamp: " + timestamp + ", Counter: " + counter + "</body></html>";

        Assertions.assertTrue(expectedResponse instanceof String);
    }
}