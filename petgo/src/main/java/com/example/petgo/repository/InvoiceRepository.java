package com.example.petgo.repository;

import com.example.petgo.entity.Invoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @EntityGraph(attributePaths = { "user", "booking", "booking.provider", "booking.pet", "booking.providerService",
            "booking.providerService.service" })
    Optional<Invoice> findByBookingId(Long bookingId);

    @EntityGraph(attributePaths = { "user", "booking", "booking.customerUser", "booking.provider", "booking.pet",
            "booking.providerService", "booking.providerService.service" })
    @Query("""
            select i
            from Invoice i
            where i.booking.provider.id = :providerId
            order by i.issuedAt desc, i.createdAt desc, i.id desc
            """)
    List<Invoice> findDetailedByProviderId(@Param("providerId") Long providerId);

    @EntityGraph(attributePaths = { "user", "booking", "booking.customerUser", "booking.provider", "booking.pet",
            "booking.providerService", "booking.providerService.service" })
    @Query("""
            select i
            from Invoice i
            where i.booking.provider.id = :providerId
              and i.id = :invoiceId
            """)
    Optional<Invoice> findDetailedByProviderIdAndInvoiceId(@Param("providerId") Long providerId,
            @Param("invoiceId") Long invoiceId);

    @EntityGraph(attributePaths = { "user", "booking", "booking.provider", "booking.pet", "booking.providerService",
            "booking.providerService.service", "membershipSubscription", "membershipSubscription.membershipPlan" })
    Optional<Invoice> findDetailedById(Long id);

    @EntityGraph(attributePaths = { "user", "membershipSubscription", "membershipSubscription.membershipPlan",
            "membershipSubscription.membershipPlan.features" })
    Optional<Invoice> findTopByMembershipSubscriptionIdOrderByCreatedAtDescIdDesc(Long membershipSubscriptionId);
}
