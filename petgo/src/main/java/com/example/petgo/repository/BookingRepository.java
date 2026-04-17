package com.example.petgo.repository;

import com.example.petgo.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"customerUser", "provider", "pet", "providerService", "providerService.service", "availabilitySlot"})
    @Query("""
            select b
            from Booking b
            where b.id = :bookingId
            """)
    Optional<Booking> findDetailedById(@Param("bookingId") Long bookingId);

    @EntityGraph(attributePaths = {"customerUser", "provider", "pet", "providerService", "providerService.service", "availabilitySlot"})
    @Query("""
            select b
            from Booking b
            where b.customerUser.id = :ownerUserId
            order by b.appointmentDate desc, b.startTime desc, b.id desc
            """)
    List<Booking> findDetailedByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    @EntityGraph(attributePaths = {"customerUser", "provider", "pet", "providerService", "providerService.service", "availabilitySlot"})
    @Query("""
            select b
            from Booking b
            where b.customerUser.id = :ownerUserId
              and b.id = :bookingId
            """)
    Optional<Booking> findDetailedOwnedById(@Param("ownerUserId") Long ownerUserId, @Param("bookingId") Long bookingId);

    long countByCustomerUser_Id(Long ownerUserId);
}
