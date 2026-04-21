package com.pharmaorder.productservice.repository;

import com.pharmaorder.productservice.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
