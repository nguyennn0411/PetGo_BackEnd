package com.example.petgo.repository;

import com.example.petgo.entity.ShippingFeeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ShippingFeeConfigRepository extends JpaRepository<ShippingFeeConfig, Long> {

    List<ShippingFeeConfig> findByAreaIdOrderByFromKmAsc(Long areaId);

    List<ShippingFeeConfig> findByAreaIdAndActiveTrueOrderByFromKmAsc(Long areaId);

    void deleteByAreaId(Long areaId);
}
