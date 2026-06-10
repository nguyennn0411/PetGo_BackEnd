package com.example.petgo.repository;

import com.example.petgo.entity.ProviderBookingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderBookingPolicyRepository extends JpaRepository<ProviderBookingPolicy, Long> {
    Optional<ProviderBookingPolicy> findByProvider_Id(Long providerId);
}