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
    private BigDecimal originalPrice; // For discounts/offers

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean prescriptionRequired;
    
    private String dosage; // e.g. "500mg"
    private String packaging; // e.g. "Strip of 10"
    
    private String status; // ACTIVE, INACTIVE, OUT_OF_STOCK
    
    private boolean isBundle;
    private String bundleItems; // Comma-separated product IDs or names for simplicity
    
    private boolean onSeasonalOffer;
    private BigDecimal seasonalDiscount;

    // Manual Builder
    public static ProductBuilder builder() { return new ProductBuilder(); }
    public static class ProductBuilder {
        private Product p = new Product();
        public ProductBuilder name(String name) { p.name = name; return this; }
        public ProductBuilder description(String desc) { p.description = desc; return this; }
        public ProductBuilder price(BigDecimal price) { p.price = price; return this; }
        public ProductBuilder originalPrice(BigDecimal op) { p.originalPrice = op; return this; }
        public ProductBuilder category(Category cat) { p.category = cat; return this; }
        public ProductBuilder prescriptionRequired(boolean pr) { p.prescriptionRequired = pr; return this; }
        public ProductBuilder dosage(String dosage) { p.dosage = dosage; return this; }
        public ProductBuilder packaging(String packaging) { p.packaging = packaging; return this; }
        public ProductBuilder status(String status) { p.status = status; return this; }
        public ProductBuilder isBundle(boolean ib) { p.isBundle = ib; return this; }
        public ProductBuilder bundleItems(String bi) { p.bundleItems = bi; return this; }
        public ProductBuilder onSeasonalOffer(boolean oso) { p.onSeasonalOffer = oso; return this; }
        public ProductBuilder seasonalDiscount(BigDecimal sd) { p.seasonalDiscount = sd; return this; }
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
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal op) { this.originalPrice = op; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean pr) { this.prescriptionRequired = pr; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getPackaging() { return packaging; }
    public void setPackaging(String packaging) { this.packaging = packaging; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isBundle() { return isBundle; }
    public void setBundle(boolean bundle) { isBundle = bundle; }

    public String getBundleItems() { return bundleItems; }
    public void setBundleItems(String bundleItems) { this.bundleItems = bundleItems; }

    public boolean isOnSeasonalOffer() { return onSeasonalOffer; }
    public void setOnSeasonalOffer(boolean onSeasonalOffer) { this.onSeasonalOffer = onSeasonalOffer; }

    public BigDecimal getSeasonalDiscount() { return seasonalDiscount; }
    public void setSeasonalDiscount(BigDecimal seasonalDiscount) { this.seasonalDiscount = seasonalDiscount; }
}
