package com.pharmaorder.productservice.controller;

import com.pharmaorder.productservice.model.entity.Category;
import com.pharmaorder.productservice.model.entity.Product;
import com.pharmaorder.productservice.repository.CategoryRepository;
import com.pharmaorder.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final com.pharmaorder.productservice.client.InventoryClient inventoryClient;

    public ProductController(ProductRepository productRepository, 
                             CategoryRepository categoryRepository,
                             com.pharmaorder.productservice.client.InventoryClient inventoryClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryClient = inventoryClient;
    }

    @GetMapping("")
    public ResponseEntity<java.util.List<com.pharmaorder.productservice.model.dto.ProductResponse>> getAllProducts(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Boolean featured) {
        
        java.util.List<Product> products = productRepository.findAll();
        if (Boolean.TRUE.equals(featured)) {
            products = products.subList(0, Math.min(products.size(), 8)); // Return more for featured if needed
        }

        // Aggregate Product IDs
        java.util.List<Long> productIds = products.stream().map(Product::getId).collect(java.util.stream.Collectors.toList());

        // Fetch Inventory Bulk
        java.util.Map<Long, Integer> inventoryMap = new java.util.HashMap<>();
        try {
            java.util.List<Object> inventories = inventoryClient.getBulkStock(productIds);
            for (Object obj : inventories) {
                // Manually map due to generic Object in client to avoid dependency on Inventory entity
                java.util.Map<String, Object> inv = (java.util.Map<String, Object>) obj;
                Long pid = ((Number) inv.get("productId")).longValue();
                Integer qty = (Integer) inv.get("availableQuantity");
                inventoryMap.put(pid, qty);
            }
        } catch (Exception e) {
            // Log error and proceed with 0 stock as fallback
            System.err.println("Failed to fetch inventory: " + e.getMessage());
        }

        // Map to Response
        java.util.List<com.pharmaorder.productservice.model.dto.ProductResponse> responses = products.stream()
                .map(p -> com.pharmaorder.productservice.model.dto.ProductResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .originalPrice(p.getOriginalPrice())
                        .category(p.getCategory())
                        .prescriptionRequired(p.isPrescriptionRequired())
                        .dosage(p.getDosage())
                        .packaging(p.getPackaging())
                        .status(p.getStatus())
                        .quantity(inventoryMap.getOrDefault(p.getId(), 0))
                        .isBundle(p.isBundle())
                        .bundleItems(p.getBundleItems())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.pharmaorder.productservice.model.dto.ProductResponse> getProductById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        java.util.Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product p = productOpt.get();
        Integer qty = 0;
        try {
            java.util.Map<String, Object> inv = (java.util.Map<String, Object>) inventoryClient.getStock(id);
            qty = (Integer) inv.get("availableQuantity");
        } catch (Exception e) {}
        
        com.pharmaorder.productservice.model.dto.ProductResponse response = com.pharmaorder.productservice.model.dto.ProductResponse.builder()
            .id(p.getId())
            .name(p.getName())
            .description(p.getDescription())
            .price(p.getPrice())
            .originalPrice(p.getOriginalPrice())
            .category(p.getCategory())
            .prescriptionRequired(p.isPrescriptionRequired())
            .dosage(p.getDosage())
            .packaging(p.getPackaging())
            .status(p.getStatus())
            .quantity(qty)
            .isBundle(p.isBundle())
            .bundleItems(p.getBundleItems())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }
}
