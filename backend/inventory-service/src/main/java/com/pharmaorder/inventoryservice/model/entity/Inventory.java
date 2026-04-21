package com.pharmaorder.inventoryservice.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;

    private Integer lowStockThreshold;

    @Version
    private Long version;

    // Manual Builder
    public static InventoryBuilder builder() { return new InventoryBuilder(); }
    public static class InventoryBuilder {
        private Inventory i = new Inventory();
        public InventoryBuilder productId(Long pid) { i.productId = pid; return this; }
        public InventoryBuilder totalQuantity(Integer t) { i.totalQuantity = t; return this; }
        public InventoryBuilder availableQuantity(Integer a) { i.availableQuantity = a; return this; }
        public InventoryBuilder reservedQuantity(Integer r) { i.reservedQuantity = r; return this; }
        public InventoryBuilder lowStockThreshold(Integer l) { i.lowStockThreshold = l; return this; }
        public Inventory build() { return i; }
    }

    // Manual Getters/Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long pid) { this.productId = pid; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer t) { this.totalQuantity = t; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer a) { this.availableQuantity = a; }
    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer r) { this.reservedQuantity = r; }
    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer l) { this.lowStockThreshold = l; }

    @JsonProperty("quantity")
    public Integer getQuantity() {
        return availableQuantity;
    }

    @JsonProperty("status")
    public String getStatus() {
        if (availableQuantity == null || availableQuantity <= 0) return "OUT_OF_STOCK";
        if (availableQuantity < (lowStockThreshold != null ? lowStockThreshold : 10)) return "LOW_STOCK";
        return "IN_STOCK";
    }
}
