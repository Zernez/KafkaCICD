package com.mqtttrifork.producer.controller;

import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@Component
public class ProducerMain implements CommandLineRunner {

    // Run the application in Spring Boot environment
    @Override
    public void run(String... args) throws Exception {
        main(args);
    }

	// Automatically inject the controller
	@Autowired
	private ProducerController consumerController;
    
    public void main(String[] args) throws InterruptedException {
        Random random = new Random();
        
        // Starts the messages transmission with random delay between 1 and 3 seconds
        while (true) {
            consumerController.publish();
            int delay = random.nextInt(20000) + 50000;
            try {
            	Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }
}