package com.pharmaorder.chatbot.dto.context;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderSummaryDto {
    private UUID id;
    private String orderStatus;
    private double totalAmount;
    private String estimatedDelivery;
    private LocalDateTime createdAt;

    public OrderSummaryDto() {}

    public OrderSummaryDto(UUID id, String orderStatus, double totalAmount, String estimatedDelivery, LocalDateTime createdAt) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.estimatedDelivery = estimatedDelivery;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
