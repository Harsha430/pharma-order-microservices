package com.pharmaorder.chatbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_rate_limits")
public class ChatRateLimit {
    @Id
    private UUID userId;
    private int messageCount;
    private LocalDateTime windowStart;

    public ChatRateLimit() {}

    public ChatRateLimit(UUID userId, int messageCount, LocalDateTime windowStart) {
        this.userId = userId;
        this.messageCount = messageCount;
        this.windowStart = windowStart;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }
}
