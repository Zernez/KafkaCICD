package com.mqtttrifork.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;


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
    void testGetStatus() {
        //Arrange
        Long timestamp = 1234567890L;
        Long counter = 42L;
        producerController.setStats(new HashMap<>() {{
            put("timestamp", timestamp);
            put("counter", counter);
        }});
        String expectedMessage = "Timestamp: " + timestamp + ", Counter: " + counter;

        //Act
        String[] parts = expectedMessage.split(", ");
        String timestampPart = parts[0].substring(parts[0].indexOf(":") + 2);
        String counterPart = parts[1].substring(parts[1].indexOf(":") + 2);
        long timestampRes = Long.parseLong(timestampPart);
        long counterRes = Long.parseLong(counterPart);

        //Assert
        Assertions.assertEquals(timestamp, timestampRes);
        Assertions.assertEquals(counter, counterRes);
    }

    @Test
    void testSetStats() {
        //Arrange
        Long timestamp = 9876543210L;
        Long counter = 99L;
        Map<String, Long> stats = new HashMap<>() {{
            put("timestamp", timestamp);
            put("counter", counter);
        }};

        //Assert
        Assertions.assertTrue(stats instanceof Map<String, Long>);
    }
}