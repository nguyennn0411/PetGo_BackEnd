package com.example.petgo.repository;

import com.example.petgo.entity.ProviderAvailabilitySlot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProviderAvailabilitySlotRepository extends JpaRepository<ProviderAvailabilitySlot, Long> {

    @EntityGraph(attributePaths = {"provider", "providerService", "providerService.service"})
    @Query("""
            select pas
            from ProviderAvailabilitySlot pas
            where pas.provider.id in :providerIds
              and pas.slotStatus = 'AVAILABLE'
              and pas.capacityBooked < pas.capacityTotal
              and pas.slotDate between :fromDate and :toDate
            order by pas.slotDate asc, pas.startTime asc, pas.id asc
            """)
    List<ProviderAvailabilitySlot> findUpcomingAvailableSlots(
            @Param("providerIds") Collection<Long> providerIds,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @EntityGraph(attributePaths = {"provider", "providerService", "providerService.service"})
    @Query("""
            select pas
            from ProviderAvailabilitySlot pas
            where pas.provider.id = :providerId
              and pas.slotStatus = 'AVAILABLE'
              and pas.capacityBooked < pas.capacityTotal
              and pas.slotDate between :fromDate and :toDate
            order by pas.slotDate asc, pas.startTime asc, pas.id asc
            """)
    List<ProviderAvailabilitySlot> findUpcomingAvailableSlotsForProvider(
            @Param("providerId") Long providerId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    @EntityGraph(attributePaths = {"provider", "providerService", "providerService.service"})
    @Query("""
            select pas
            from ProviderAvailabilitySlot pas
            where pas.id = :slotId
            """)
    Optional<ProviderAvailabilitySlot> findDetailedById(@Param("slotId") Long slotId);

    @EntityGraph(attributePaths = {"provider", "providerService", "providerService.service"})
    List<ProviderAvailabilitySlot> findByProvider_IdOrderBySlotDateAscStartTimeAscIdAsc(Long providerId);

    @EntityGraph(attributePaths = {"provider", "providerService", "providerService.service"})
    List<ProviderAvailabilitySlot> findByProvider_IdAndSlotDateBetweenOrderBySlotDateAscStartTimeAscIdAsc(
            Long providerId,
            LocalDate fromDate,
            LocalDate toDate
    );

}