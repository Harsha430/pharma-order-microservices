package com.pharmaorder.chatbot.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionDto {
    private UUID id;
    private String title;
    private LocalDateTime lastActive;

    public SessionDto() {}

    public SessionDto(UUID id, String title, LocalDateTime lastActive) {
        this.id = id;
        this.title = title;
        this.lastActive = lastActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }
}
