package com.example.petgo.repository;

import com.example.petgo.entity.ServiceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {
    List<ServiceReview> findByServiceIdOrderByCreatedAtDesc(Long serviceId);
    List<ServiceReview> findByServiceIdAndHiddenFalseOrderByCreatedAtDesc(Long serviceId);
    Optional<ServiceReview> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);
}
