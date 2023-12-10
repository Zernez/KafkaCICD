package com.mqtttrifork.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mqtttrifork.consumer.controller.ConsumerController;
import com.mqtttrifork.consumer.dto.ConsumerDTO;
import com.mqtttrifork.consumer.service.ConsumerService;

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
    	//Arrange
        ConsumerRecords<String, String> expectedRecords = consumerController.consumeMessages();
        
        //Act
        ConsumerRecords<String, String> actualRecords = consumerService.consumeMessages(any());
        
        //Assert
        assertEquals(expectedRecords, actualRecords);
    }

    @Test
    void testRePublish() {
        //Assert
        assertDoesNotThrow(() -> consumerController.rePublish());
        verify(consumerService, times(1)).rePublish(any(), any());
    }

    @Test
    void testSaveMessage() throws SQLException {
        //Act and Assert
        assertDoesNotThrow(() -> consumerController.saveMessage());
        verify(consumerService, times(1)).saveMessage(any(), any());
    }

    @Test
    public void testCheckConsistence() throws SQLException {
        //Arrange
        String expectedDbMessages = "Consumer is running and the last 3 messages published on the DB are: \nTimestamp: Test, Counter: Test\nLast timestamp and counter are: 0 and 0"; 
        String stubMessage = "Timestamp: Test, Counter: Test";
        doReturn(stubMessage).when(consumerService).getLastThreeMessages(any());
        ResponseEntity<String> response = consumerController.checkConsistence();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDbMessages, response.getBody());
    }
}

