package com.mqtttrifork.consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mqtttrifork.consumer.dto.ConsumerDTO;

public class ConsumerDTOTest {

    @Test
    public void testExtractTimestamp() {
        //Arrange
        ConsumerDTO consumerDTO = new ConsumerDTO();
        String message = "Timestamp: 1629876543210, Counter: 42";
        long expectedTimestamp = 1629876543210L;

        //Act
        long actualTimestamp = consumerDTO.extractTimestamp(message);

        //Assert
        Assertions.assertEquals(expectedTimestamp, actualTimestamp);
        Assertions.assertEquals(expectedTimestamp, consumerDTO.getTimestamp());
        Assertions.assertEquals(42L, consumerDTO.getMessage());
    }

    @Test
    public void testIsOneMinuteOld() {
        //Arrange
        ConsumerDTO consumerDTO = new ConsumerDTO();
        long currentTimestamp = System.currentTimeMillis();
        long beforeOneMinAgo = currentTimestamp - 10;
        long twoMinutesAgo = currentTimestamp - 120000;

        //Assert
        Assertions.assertFalse(consumerDTO.isOneMinuteOld(currentTimestamp));
        Assertions.assertFalse(consumerDTO.isOneMinuteOld(beforeOneMinAgo));
        Assertions.assertTrue(consumerDTO.isOneMinuteOld(twoMinutesAgo));
    }

    @Test
    public void testIsOdd() {
        //Arrange & act
        ConsumerDTO consumerDTO = new ConsumerDTO();

        //Assert
        Assertions.assertTrue(consumerDTO.isOdd(5L));
        Assertions.assertFalse(consumerDTO.isOdd(10L));
        Assertions.assertTrue(consumerDTO.isOdd(99999999999L));
        Assertions.assertFalse(consumerDTO.isOdd(1000000000L));
    }
}