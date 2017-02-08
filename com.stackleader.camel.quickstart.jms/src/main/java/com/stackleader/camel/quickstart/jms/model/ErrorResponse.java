package com.stackleader.camel.quickstart.jms.model;

import java.util.List;

public class ErrorResponse {
    private final List<Message> messages;

    public ErrorResponse(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
    
    
}
