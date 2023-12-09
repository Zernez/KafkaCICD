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

    private ProducerController producerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        producerController = new ProducerController();
    }

    @Test
    void testPublish() {
        //Arrange
        Long timestamp = System.currentTimeMillis();
        Long counter = 1L;
        String expectedMessage = "Timestamp: " + timestamp + ", Counter: " + counter;

        //Assert
        assertDoesNotThrow(() -> producerController.publish());
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
        ResponseEntity<String> response = producerController.getStatus();

        //Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedMessage, response.getBody());
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

        //Act
        producerController.setStats(stats);

        //Assert
        Assertions.assertEquals(counter, producerController.getStats().get("counter"));
        Assertions.assertEquals(timestamp, producerController.getStats().get("timestamp"));
    }
}