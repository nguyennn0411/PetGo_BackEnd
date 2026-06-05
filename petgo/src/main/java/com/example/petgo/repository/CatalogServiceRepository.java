package com.example.petgo.repository;

import com.example.petgo.entity.CatalogService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatalogServiceRepository extends JpaRepository<CatalogService, Long> {

    @EntityGraph(attributePaths = {"category", "category.parent"})
    List<CatalogService> findByActiveTrueOrderByNameAscIdAsc();

    @EntityGraph(attributePaths = {"category", "category.parent"})
    @Query("""
            select s
            from CatalogService s
            where s.id = :serviceId
              and s.active = true
              and s.category.active = true
            """)
    Optional<CatalogService> findActiveDetailById(@Param("serviceId") Long serviceId);
}