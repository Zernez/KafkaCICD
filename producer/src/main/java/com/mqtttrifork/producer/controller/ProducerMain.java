package com.mqtttrifork.producer.controller;

import java.util.Random;

public class ProducerMain {
    public static void main(String[] args) throws InterruptedException {
        ProducerController pc = new ProducerController();
        Random random = new Random();
        
        // Starts the messages transmission with random delay between 1 and 3 seconds
        while (true) {
            pc.publish();
            int delay = random.nextInt(20000) + 50000;
            try {
            	Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }
}