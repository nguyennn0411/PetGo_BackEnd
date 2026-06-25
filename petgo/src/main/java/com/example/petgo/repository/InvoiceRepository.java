package com.example.petgo.repository;

import com.example.petgo.entity.Invoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @EntityGraph(attributePaths = { "user", "membershipSubscription", "membershipSubscription.membershipPlan",
            "membershipSubscription.membershipPlan.features" })
    Optional<Invoice> findDetailedById(Long id);

    @EntityGraph(attributePaths = { "user", "membershipSubscription", "membershipSubscription.membershipPlan",
            "membershipSubscription.membershipPlan.features" })
    Optional<Invoice> findTopByMembershipSubscriptionIdOrderByCreatedAtDescIdDesc(Long membershipSubscriptionId);
}