package com.example.petgo.repository;

import com.example.petgo.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"product", "product.category"})
    List<CartItem> findByUser_IdOrderByIdDesc(Long userId);
    Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteByUser_Id(Long userId);
}
