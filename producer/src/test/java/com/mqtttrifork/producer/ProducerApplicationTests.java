package com.mqtttrifork.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.mqtttrifork.producer.controller.ProducerController;


@SpringJUnitConfig
@SpringBootTest
class ProducerControllerTest {

    private ProducerController producerController;

    @Mock
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        producerController = new ProducerController();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testPublish() {
        // Arrange
        Map<String, Long> stats = new HashMap<>();
        stats.put("counter", (long) 5);
        stats.put("nMessages", (long) 5);
        producerController.setStats(stats);

        // Act
        producerController.publish();

        // Assert
        verify(kafkaProducer, times(1)).send(any(ProducerRecord.class));
        assertEquals((long) 6, stats.get("counter"));
        assertEquals((long) 6, stats.get("nMessages"));
    }

    @Test
    void testGetStatus() {
        // Arrange
        Map<String, Long> stats = new HashMap<>();
        stats.put("nMessages", (long) 10);
        producerController.setStats(stats);

        // Act
        ResponseEntity<Long> response = producerController.getStatus();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(10, response.getBody());
    }

    @Test
    void testGetStatusWithSpringBootTest() throws Exception {
        // Act and assert
        mockMvc.perform(get("/status"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("10"));
    }
}
