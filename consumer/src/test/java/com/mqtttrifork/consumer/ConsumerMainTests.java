package com.mqtttrifork.consumer;


import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import com.mqtttrifork.consumer.controller.ConsumerController;
import com.mqtttrifork.consumer.controller.ConsumerMain;

class ConsumerMainTest {

    private ConsumerMain consumerMain;

    @Mock
    private ConsumerController consumerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consumerMain = new ConsumerMain();

    }

    @Test
    void testMain() throws SQLException {
        // Arrange
        ConsumerRecords<String, String> records = new ConsumerRecords<String, String>(null);
        String message = "Timestamp: " + "0L" + ", Counter: " + "0L";
        long timestamp = 123456789L;

        when(consumerController.consumeMessages()).thenReturn(records);
        when(consumerController.extractTimestamp(message)).thenReturn(timestamp);
        when(consumerController.isOneMinuteOld(timestamp)).thenReturn(true);
        when(consumerController.isOdd(timestamp)).thenReturn(true);

        // Act
        consumerMain.main(null);

        // Assert
        verify(consumerController, times(1)).connectToDB("db");
        verify(consumerController, times(1)).setUpConsumerKafka();
        verify(consumerController, times(1)).setUpProducerKafka();
        verify(consumerController, times(1)).consumeMessages();
        verify(consumerController, times(1)).extractTimestamp(message);
        verify(consumerController, times(1)).isOneMinuteOld(timestamp);
        verify(consumerController, times(1)).isOdd(timestamp);
        verify(consumerController, times(1)).rePublish();
        verify(consumerController, never()).saveMessage();
    }
}