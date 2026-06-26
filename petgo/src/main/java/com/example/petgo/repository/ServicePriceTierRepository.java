package com.example.petgo.repository;

import com.example.petgo.entity.ServicePriceTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicePriceTierRepository extends JpaRepository<ServicePriceTier, Long> {
    List<ServicePriceTier> findByServiceIdOrderBySpeciesAscWeightFromAsc(Long serviceId);

    void deleteByServiceId(Long serviceId);
}
