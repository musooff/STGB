package com.mnm.georemider;

import java.util.Date;

public class ChatMessage {

    private String to;
    private String from;
    private String messageText;
    private long messageTime;
 
    public ChatMessage(String to, String from, String messageText, long messageTime) {
        this.to = to;
        this.from = from;
        this.messageText = messageText;
        this.messageTime = messageTime;
        // Initialize to current time
    }

 
    public String getMessageText() {
        return messageText;
    }


    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public long getMessageTime() {
        return messageTime;
    }

}