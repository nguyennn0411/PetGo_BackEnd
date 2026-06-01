package com.example.petgo.repository;

import com.example.petgo.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = "category")
    Optional<Product> findBySlugAndDeletedAtIsNull(String slug);

    @EntityGraph(attributePaths = "category")
    List<Product> findByDeletedAtIsNullOrderByIdDesc();

    boolean existsBySlug(String slug);
    boolean existsBySku(String sku);
}
