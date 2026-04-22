package com.pharmaorder.chatbot.dto.request;

import java.util.UUID;

public class ChatRequest {
    private UUID sessionId;
    private String message;

    public ChatRequest() {}

    public ChatRequest(UUID sessionId, String message) {
        this.sessionId = sessionId;
        this.message = message;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
