package com.example.petgo.repository;

import com.example.petgo.entity.ShopOrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOrderStatusHistoryRepository extends JpaRepository<ShopOrderStatusHistory, Long> {
}
