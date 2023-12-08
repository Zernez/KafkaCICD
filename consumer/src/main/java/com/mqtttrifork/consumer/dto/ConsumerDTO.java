package com.mqtttrifork.consumer.dto;

public class ConsumerDTO {

    private long timestamp;
    private long message;
    
    // Start condition
    public ConsumerDTO(){
    	this.timestamp = 0;
    	this.message = 0;
    }
    
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getMessage() {
		return message;
	}
	public void setMessage(long message) {
		this.message = message;
	}
	
    //Extract the information from the string, save and return the timestamp
	public long extractTimestamp(String message) {
        // Extract the timestamp from the message
        // Assuming the message format is "Timestamp: <timestamp>, Counter: <counter>"
        String[] parts = message.split(", ");
        String timestampPart = parts[0].substring(parts[0].indexOf(":") + 2);
        String counterPart = parts[1].substring(parts[1].indexOf(":") + 2);

        this.timestamp = Long.parseLong(timestampPart);
        this.message = Long.parseLong(counterPart);
        
        return this.timestamp;
    }
    
	// Check if the timestamp is older than 1 minute
    public boolean isOneMinuteOld(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return currentTime - timestamp >= 60000;
    }

    // Check if the number is odd
    public boolean isOdd(long number) {
        return number % 2 != 0;
    }
}
