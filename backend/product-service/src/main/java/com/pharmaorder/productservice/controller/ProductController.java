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

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Product>> getAllProducts(@org.springframework.web.bind.annotation.RequestParam(required = false) Boolean featured) {
        if (Boolean.TRUE.equals(featured)) {
            // For now return limited list if featured, normally this would be a specific query
            List<Product> products = productRepository.findAll();
            return ResponseEntity.ok(products.subList(0, Math.min(products.size(), 3)));
        }
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }
}
