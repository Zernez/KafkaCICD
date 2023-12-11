package com.mqtttrifork.consumer.dto;

public class ConsumerDTO {

    private long timestamp;
    private long message;
    private String timestampToDB;
    private String messageToDB;
    private String timestampToResend;
    private String messageToResend;
    
    // Start condition
    public ConsumerDTO(){
    	this.timestamp = 0;
    	this.message = 0;
    	this.timestampToDB = "";
    	this.messageToDB = "";
    	this.timestampToResend = "";
    	this.messageToResend = "";
    }
    
    public String getTimestampToResend() {
        return this.timestampToResend;
    }

    public void setTimestampToResend(String timestampToResend) {
        this.timestampToResend = timestampToResend;
    }

    public String getMessageToResend() {
        return this.messageToResend;
    }

    public void setMessageToResend(String messageToResend) {
        this.messageToResend = messageToResend;
    }

    public String getTimestampToDB() {
        return this.timestampToDB;
    }

    public void setTimestampToDB(String timestampToDB) {
        this.timestampToDB = timestampToDB;
    }

    public String getMessageToDB() {
        return this.messageToDB;
    }

    public void setMessageToDB(String messageToDB) {
        this.messageToDB = messageToDB;
    }

	public long getTimestamp() {
		return this.timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getMessage() {
		return this.message;
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
        
        return Long.parseLong(timestampPart);
    }
    
	// Check if the timestamp is older than 1 minute
    public boolean isOneMinuteOld(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return currentTime - timestamp >= 60000;
    }

    // Check if the number is odd
    public boolean isOdd(long number) {
        return number % 2L != 0L;
    }
}
