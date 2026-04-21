package com.pharmaorder.productservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "categories")
@NoArgsConstructor @AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    
    private String slug;

    // Manual Builder
    public static CategoryBuilder builder() { return new CategoryBuilder(); }
    public static class CategoryBuilder {
        private Category c = new Category();
        public CategoryBuilder name(String name) { c.name = name; return this; }
        public CategoryBuilder slug(String slug) { c.slug = slug; return this; }
        public Category build() { return c; }
    }

    // Manual Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}
