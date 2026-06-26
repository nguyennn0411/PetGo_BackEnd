package com.example.petgo.repository;

import com.example.petgo.entity.ShopOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM ShopOrder o WHERE o.status IN ('PAID', 'PACKING', 'SHIPPING', 'COMPLETED')")
    java.math.BigDecimal sumTotalAmountByRevenueStatuses();

    @Query("SELECT COUNT(o) FROM ShopOrder o WHERE o.status IN ('PAID', 'PACKING', 'SHIPPING', 'COMPLETED')")
    long countByRevenueStatuses();

    @Query(value = "SELECT CAST(o.created_at AS DATE) AS day, COALESCE(SUM(o.total_amount), 0) AS total FROM shop_orders o WHERE o.status IN ('PAID','PACKING','SHIPPING','COMPLETED') AND o.created_at >= :since GROUP BY CAST(o.created_at AS DATE) ORDER BY day", nativeQuery = true)
    List<Object[]> dailyShopRevenueSince(@Param("since") LocalDateTime since);

    @Query(value = "SELECT COALESCE(SUM(o.total_amount), 0) FROM shop_orders o WHERE o.status IN ('PAID','PACKING','SHIPPING','COMPLETED') AND o.created_at BETWEEN :from AND :to", nativeQuery = true)
    java.math.BigDecimal shopRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
