package com.pharmaorder.productservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean prescriptionRequired;
    
    private String status; // ACTIVE, INACTIVE, OUT_OF_STOCK

    // Manual Builder
    public static ProductBuilder builder() { return new ProductBuilder(); }
    public static class ProductBuilder {
        private Product p = new Product();
        public ProductBuilder name(String name) { p.name = name; return this; }
        public ProductBuilder description(String desc) { p.description = desc; return this; }
        public ProductBuilder price(BigDecimal price) { p.price = price; return this; }
        public ProductBuilder category(Category cat) { p.category = cat; return this; }
        public ProductBuilder prescriptionRequired(boolean pr) { p.prescriptionRequired = pr; return this; }
        public ProductBuilder status(String status) { p.status = status; return this; }
        public Product build() { return p; }
    }

    // Manual Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean pr) { this.prescriptionRequired = pr; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
