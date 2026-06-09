package com.example.petgo.repository;

import com.example.petgo.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = { "customerUser", "provider", "provider.user", "providerService" })
    List<Booking> findByStatusOrderByUpdatedAtAscIdAsc(String status);

    @EntityGraph(attributePaths = { "customerUser", "provider", "provider.user", "providerService" })
    List<Booking> findByStatusInOrderByUpdatedAtAscIdAsc(List<String> statuses);

    @EntityGraph(attributePaths = { "customerUser", "provider", "pet", "providerService", "providerService.service",
            "availabilitySlot" })
    @Query("""
            select b
            from Booking b
            where b.id = :bookingId
            """)
    Optional<Booking> findDetailedById(@Param("bookingId") Long bookingId);

    @EntityGraph(attributePaths = { "customerUser", "provider", "pet", "providerService", "providerService.service",
            "availabilitySlot" })
    @Query("""
            select b
            from Booking b
            where b.customerUser.id = :ownerUserId
            order by b.appointmentDate desc, b.startTime desc, b.id desc
            """)
    List<Booking> findDetailedByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    @EntityGraph(attributePaths = { "customerUser", "provider", "pet", "providerService", "providerService.service",
            "availabilitySlot" })
    @Query("""
            select b
            from Booking b
            where b.customerUser.id = :ownerUserId
              and b.id = :bookingId
            """)
    Optional<Booking> findDetailedOwnedById(@Param("ownerUserId") Long ownerUserId,
            @Param("bookingId") Long bookingId);

    @EntityGraph(attributePaths = { "customerUser", "provider", "pet", "providerService", "providerService.service",
            "providerService.service.category", "availabilitySlot" })
    @Query("""
            select b
            from Booking b
            where b.provider.id = :providerId
            order by b.appointmentDate desc, b.startTime desc, b.id desc
            """)
    List<Booking> findDetailedByProviderId(@Param("providerId") Long providerId);

    @EntityGraph(attributePaths = { "customerUser", "provider", "pet", "providerService", "providerService.service",
            "availabilitySlot" })
    @Query("""
            select b
            from Booking b
            where b.provider.id = :providerId
              and b.id = :bookingId
            """)
    Optional<Booking> findDetailedByProviderIdAndBookingId(@Param("providerId") Long providerId,
            @Param("bookingId") Long bookingId);

    long countByCustomerUser_Id(Long ownerUserId);

    long countByCustomerUser_IdAndStatus(Long ownerUserId, String status);

    long countByProviderService_Id(Long providerServiceId);

    boolean existsByProviderService_Id(Long providerServiceId);

    @Query("""
            select count(b)
            from Booking b
            where b.provider.id = :providerId
              and b.providerService.id = :providerServiceId
              and b.appointmentDate = :appointmentDate
              and b.status in ('PENDING_PROVIDER_CONFIRMATION', 'PENDING_CONFIRMATION', 'CONFIRMED', 'IN_PROGRESS', 'AWAITING_COMPLETION_CONFIRMATION', 'COMPLETED_BY_USER', 'COMPLETED_BY_PROVIDER', 'DISPUTED', 'PENDING_PAYMENT', 'PAID')
              and b.startTime < :endTime
              and b.endTime > :startTime
            """)
    long countActiveOverlappingBookings(@Param("providerId") Long providerId,
            @Param("providerServiceId") Long providerServiceId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("""
            select count(b)
            from Booking b
            where b.provider.id = :providerId
              and b.appointmentDate = :appointmentDate
              and b.status in ('PENDING_PROVIDER_CONFIRMATION', 'PENDING_CONFIRMATION', 'CONFIRMED', 'IN_PROGRESS', 'AWAITING_COMPLETION_CONFIRMATION', 'COMPLETED_BY_USER', 'COMPLETED_BY_PROVIDER', 'DISPUTED', 'PENDING_PAYMENT', 'PAID')
              and (:startTime is null or :endTime is null or (b.startTime < :endTime and b.endTime > :startTime))
            """)
    long countActiveProviderBookingsOnDate(@Param("providerId") Long providerId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}
