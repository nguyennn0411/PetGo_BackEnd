package com.example.petgo.repository;

import com.example.petgo.entity.BookingLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingLockRepository extends JpaRepository<BookingLock, Long> {

    @Query("""
            select l
            from BookingLock l
            join fetch l.provider
            join fetch l.providerService ps
            join fetch l.user
            where l.id = :lockId
            """)
    Optional<BookingLock> findDetailedById(@Param("lockId") Long lockId);

    @Query("""
            select l
            from BookingLock l
            join fetch l.provider
            join fetch l.providerService ps
            join fetch l.user
            where l.provider.id = :providerId
              and (:activeOnly = false or (l.status = 'ACTIVE' and l.expiresAtUtc > :nowUtc))
            order by l.appointmentDate asc, l.startTime asc, l.id desc
            """)
    List<BookingLock> findByProviderForManagement(@Param("providerId") Long providerId,
            @Param("activeOnly") boolean activeOnly,
            @Param("nowUtc") LocalDateTime nowUtc);

    @Query("""
            select l
            from BookingLock l
            where l.provider.id = :providerId
              and l.providerService.id = :providerServiceId
              and l.appointmentDate = :appointmentDate
              and l.status = 'ACTIVE'
              and l.expiresAtUtc > :nowUtc
              and l.startTime < :endTime
              and l.endTime > :startTime
            """)
    List<BookingLock> findActiveOverlappingLocks(@Param("providerId") Long providerId,
            @Param("providerServiceId") Long providerServiceId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("nowUtc") LocalDateTime nowUtc);

    @Query("""
            select count(l)
            from BookingLock l
            where l.provider.id = :providerId
              and l.providerService.id = :providerServiceId
              and l.appointmentDate = :appointmentDate
              and l.status = 'ACTIVE'
              and l.expiresAtUtc > :nowUtc
              and l.startTime < :endTime
              and l.endTime > :startTime
              and (:excludeOwnerUserId is null or l.user is null or l.user.id <> :excludeOwnerUserId)
            """)
    long countActiveOverlappingLocks(@Param("providerId") Long providerId,
            @Param("providerServiceId") Long providerServiceId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("nowUtc") LocalDateTime nowUtc,
            @Param("excludeOwnerUserId") Long excludeOwnerUserId);
}