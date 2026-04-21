package com.pharmaorder.orderservice.model.entity;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private String userEmail;
    
    private UUID prescriptionId; // Nullable if not required

    private BigDecimal totalAmount;
    private Integer pointsRedeemed;
    private BigDecimal discountAmount;
    
    private String status; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    
    @JsonProperty("createdAt")
    private LocalDateTime placedAt;
    
    @JsonProperty("orderItems")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    // Manual Builder
    public static OrderBuilder builder() { return new OrderBuilder(); }
    public static class OrderBuilder {
        private Order o = new Order();
        public OrderBuilder userId(UUID userId) { o.userId = userId; return this; }
        public OrderBuilder userEmail(String email) { o.userEmail = email; return this; }
        public OrderBuilder prescriptionId(UUID pid) { o.prescriptionId = pid; return this; }
        public OrderBuilder totalAmount(BigDecimal amount) { o.totalAmount = amount; return this; }
        public OrderBuilder status(String status) { o.status = status; return this; }
        public OrderBuilder placedAt(LocalDateTime at) { o.placedAt = at; return this; }
        public Order build() { return o; }
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String email) { this.userEmail = email; }
    public UUID getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(UUID pid) { this.prescriptionId = pid; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal amount) { this.totalAmount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime at) { this.placedAt = at; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public Integer getPointsRedeemed() { return pointsRedeemed; }
    public void setPointsRedeemed(Integer p) { this.pointsRedeemed = p; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal d) { this.discountAmount = d; }
}
