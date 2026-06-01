package com.example.petgo.repository;

import com.example.petgo.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByActiveTrueOrderBySortOrderAscIdAsc();
    Optional<ProductCategory> findBySlug(String slug);
}
