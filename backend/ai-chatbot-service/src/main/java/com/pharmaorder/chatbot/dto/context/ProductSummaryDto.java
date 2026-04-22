package com.pharmaorder.chatbot.dto.context;

import java.util.UUID;

public class ProductSummaryDto {
    private UUID id;
    private String name;
    private double currentPrice;
    private boolean prescriptionRequired;
    private String description;
    private String dosage;
    private String status;

    public ProductSummaryDto() {}

    public ProductSummaryDto(UUID id, String name, double currentPrice, boolean prescriptionRequired, String status, String description, String dosage) {
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.prescriptionRequired = prescriptionRequired;
        this.status = status;
        this.description = description;
        this.dosage = dosage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
