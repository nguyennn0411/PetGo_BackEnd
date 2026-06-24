package com.example.petgo.repository;

import com.example.petgo.entity.PromoCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    @EntityGraph(attributePaths = {"provider", "createdByUser"})
    Optional<PromoCode> findByCodeIgnoreCaseAndActiveTrue(String code);

    @EntityGraph(attributePaths = {"provider", "createdByUser"})
    Optional<PromoCode> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    @EntityGraph(attributePaths = {"provider", "createdByUser"})
    List<PromoCode> findAllByOrderByCreatedAtDescIdDesc();

    @EntityGraph(attributePaths = {"provider", "createdByUser"})
    List<PromoCode> findByProvider_IdOrderByCreatedAtDescIdDesc(Long providerId);

    @EntityGraph(attributePaths = {"provider", "createdByUser"})
    Optional<PromoCode> findByIdAndProvider_Id(Long id, Long providerId);
}
