package com.pharmaorder.productservice.model.dto;

import com.pharmaorder.productservice.model.entity.Category;
import java.math.BigDecimal;

public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Category category;
    private boolean prescriptionRequired;
    private String dosage;
    private String packaging;
    private String status;
    private Integer quantity;
    private boolean isBundle;
    private String bundleItems;

    // Getters needed for Jackson serialisation
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public Category getCategory() { return category; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public String getDosage() { return dosage; }
    public String getPackaging() { return packaging; }
    public String getStatus() { return status; }
    public Integer getQuantity() { return quantity; }
    public boolean isBundle() { return isBundle; }
    public String getBundleItems() { return bundleItems; }

    // Manual builder (Lombok @Builder conflicts with hand-written one)
    public static ProductResponseBuilder builder() { return new ProductResponseBuilder(); }
    public static class ProductResponseBuilder {
        private final ProductResponse r = new ProductResponse();
        public ProductResponseBuilder id(Long id) { r.id = id; return this; }
        public ProductResponseBuilder name(String name) { r.name = name; return this; }
        public ProductResponseBuilder description(String d) { r.description = d; return this; }
        public ProductResponseBuilder price(BigDecimal p) { r.price = p; return this; }
        public ProductResponseBuilder originalPrice(BigDecimal op) { r.originalPrice = op; return this; }
        public ProductResponseBuilder category(Category c) { r.category = c; return this; }
        public ProductResponseBuilder prescriptionRequired(boolean pr) { r.prescriptionRequired = pr; return this; }
        public ProductResponseBuilder dosage(String d) { r.dosage = d; return this; }
        public ProductResponseBuilder packaging(String p) { r.packaging = p; return this; }
        public ProductResponseBuilder status(String s) { r.status = s; return this; }
        public ProductResponseBuilder quantity(Integer q) { r.quantity = q; return this; }
        public ProductResponseBuilder isBundle(boolean b) { r.isBundle = b; return this; }
        public ProductResponseBuilder bundleItems(String bi) { r.bundleItems = bi; return this; }
        public ProductResponse build() { return r; }
    }
}
