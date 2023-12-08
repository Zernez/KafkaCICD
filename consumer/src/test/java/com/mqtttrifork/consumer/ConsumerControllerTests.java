package com.mqtttrifork.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.mqtttrifork.consumer.controller.ConsumerController;
import com.mqtttrifork.consumer.dto.ConsumerDTO;
import com.mqtttrifork.consumer.service.ConsumerService;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerControllerTest {

    @Mock
    private ConsumerService consumerService;
    
    @Mock
    private ConsumerDTO consumerDTO;

    @InjectMocks
    private ConsumerController consumerController;

    @Test
    void testConsumeMessages() {
    	// Arrange
        ConsumerRecords<String, String> expectedRecords = consumerController.consumeMessages();
        // Assert
        when(consumerService.consumeMessages(any())).thenReturn(expectedRecords);
    }

    @Test
    void testRePublish() {
    	// Arrange
        assertDoesNotThrow(() -> consumerController.rePublish());
        // Assert
        verify(consumerService, times(1)).rePublish(any(), any());
    }

    @Test
    void testSaveMessage() throws SQLException {
    	
    	// Assert
        doNothing().when(consumerService).saveMessage(any(), any());
        assertDoesNotThrow(() -> consumerController.saveMessage());
        verify(consumerService, times(1)).saveMessage(any(), any());
    }

    @Test
    void testExtractTimestamp() {
    	// Arrange
        long timestamp = System.currentTimeMillis();
        long counter = 1L;
        String message = "Timestamp: " + timestamp + ", Counter: " + counter;
        long expectedTimestamp = timestamp;
        long actualTimestamp = consumerController.extractTimestamp(message);
        
        // Assert
        when(consumerDTO.extractTimestamp(message)).thenReturn(expectedTimestamp);
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    @Test
    void testIsOneMinuteOld() {
    	// Arrange   	
        long currentTime = System.currentTimeMillis();
        long timestampWithinOneMinute = currentTime;
        long timestampOlderThanOneMinute = currentTime - 120;
        
        // Assert
        assertTrue(consumerController.isOneMinuteOld(timestampWithinOneMinute));
        assertFalse(consumerController.isOneMinuteOld(timestampOlderThanOneMinute));
    }

    @Test
    void testIsOdd() {
    	// Arrange
        long oddNumber = 5L;
        long evenNumber = 8L;
        
        // Assert
        assertTrue(consumerController.isOdd(oddNumber));
        assertFalse(consumerController.isOdd(evenNumber));
    }

    @Test
    void testConnectToDB() throws SQLException {
    	// Arrange
        String host = "db";
        
        // Assert
        verify(consumerService, times(1)).connectToDB(host);
        assertNotNull(consumerService.connectToDB(host)); // Assuming you have a getter for connDB
    }
}

