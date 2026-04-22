package com.pharmaorder.chatbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {
    @Id
    private UUID id;
    private UUID userId;
    private String sessionTitle;
    private LocalDateTime createdAt;
    private LocalDateTime lastActive;
    private boolean isActive;

    public ChatSession() {}

    public ChatSession(UUID id, UUID userId, String sessionTitle, LocalDateTime createdAt, LocalDateTime lastActive, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.sessionTitle = sessionTitle;
        this.createdAt = createdAt;
        this.lastActive = lastActive;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
