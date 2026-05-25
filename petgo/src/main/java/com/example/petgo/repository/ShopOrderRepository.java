package com.example.petgo.repository;

import com.example.petgo.entity.ShopOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    @EntityGraph(attributePaths = {"items", "items.product", "customerUser"})
    List<ShopOrder> findByCustomerUser_IdOrderByIdDesc(Long userId);

    @EntityGraph(attributePaths = {"items", "items.product", "customerUser"})
    Optional<ShopOrder> findByOrderCode(String orderCode);

    @EntityGraph(attributePaths = {"items", "items.product", "customerUser"})
    List<ShopOrder> findByStatusOrderByIdDesc(String status);

    @EntityGraph(attributePaths = {"items", "items.product", "customerUser"})
    List<ShopOrder> findAllByOrderByIdDesc();
}
