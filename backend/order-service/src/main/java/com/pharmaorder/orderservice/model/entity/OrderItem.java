package com.pharmaorder.orderservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor @AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Order order;

    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;

    // Manual Builder
    public static OrderItemBuilder builder() { return new OrderItemBuilder(); }
    public static class OrderItemBuilder {
        private OrderItem i = new OrderItem();
        public OrderItemBuilder order(Order order) { i.order = order; return this; }
        public OrderItemBuilder productId(Long pid) { i.productId = pid; return this; }
        public OrderItemBuilder quantity(Integer q) { i.quantity = q; return this; }
        public OrderItemBuilder unitPrice(BigDecimal up) { i.unitPrice = up; return this; }
        public OrderItem build() { return i; }
    }

    // Manual Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Long getProductId() { return productId; }
    public void setProductId(Long pid) { this.productId = pid; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer q) { this.quantity = q; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal up) { this.unitPrice = up; }
}
