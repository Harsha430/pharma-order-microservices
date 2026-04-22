package com.pharmaorder.chatbot.dto.context;

import java.time.LocalDateTime;
import java.util.UUID;

public class PrescriptionSummaryDto {
    private UUID id;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime expiryDate;
    private UUID linkedOrderId;

    public PrescriptionSummaryDto() {}

    public PrescriptionSummaryDto(UUID id, String status, LocalDateTime uploadedAt, LocalDateTime expiryDate, UUID linkedOrderId) {
        this.id = id;
        this.status = status;
        this.uploadedAt = uploadedAt;
        this.expiryDate = expiryDate;
        this.linkedOrderId = linkedOrderId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public UUID getLinkedOrderId() {
        return linkedOrderId;
    }

    public void setLinkedOrderId(UUID linkedOrderId) {
        this.linkedOrderId = linkedOrderId;
    }
}
