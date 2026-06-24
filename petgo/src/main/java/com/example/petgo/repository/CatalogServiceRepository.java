package com.example.petgo.repository;

import com.example.petgo.entity.CatalogService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatalogServiceRepository extends JpaRepository<CatalogService, Long> {

  @EntityGraph(attributePaths = { "categories" })
  List<CatalogService> findByActiveTrueOrderByNameAscIdAsc();

  @EntityGraph(attributePaths = { "categories" })
  @Query("""
      select distinct s
      from CatalogService s
      join s.categories c
      where s.active = true
        and c.active = true
      order by s.name asc, s.id asc
      """)
  List<CatalogService> findActiveDetails();

  @EntityGraph(attributePaths = { "categories" })
  @Query("""
      select s
      from CatalogService s
      join s.categories c
      where s.id = :serviceId
        and s.active = true
        and c.active = true
      """)
  Optional<CatalogService> findActiveDetailById(@Param("serviceId") Long serviceId);

  @Query("""
      select s
      from CatalogService s
      join s.categories c
      where lower(s.name) = lower(:name)
        and c.id = :categoryId
        and s.active = true
      order by s.id asc
      """)
  List<CatalogService> findActiveByNameAndCategory(@Param("name") String name,
      @Param("categoryId") Long categoryId);

  boolean existsBySlug(String slug);

  boolean existsByServiceCode(String serviceCode);

  List<CatalogService> findByCategories_Id(Long categoryId);

  long countByCategories_Id(Long categoryId);
}
