package com.example.petgo.repository;

import com.example.petgo.entity.ShippingBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ShippingBookingRepository extends JpaRepository<ShippingBooking, Long> {

    @Query("SELECT sb FROM ShippingBooking sb JOIN FETCH sb.user JOIN FETCH sb.pet LEFT JOIN FETCH sb.area LEFT JOIN FETCH sb.service WHERE sb.user.id = :userId ORDER BY sb.createdAt DESC")
    List<ShippingBooking> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT sb FROM ShippingBooking sb JOIN FETCH sb.user JOIN FETCH sb.pet LEFT JOIN FETCH sb.area LEFT JOIN FETCH sb.service ORDER BY sb.createdAt DESC")
    List<ShippingBooking> findAllOrderByCreatedAtDesc();

    @Query("SELECT sb FROM ShippingBooking sb JOIN FETCH sb.user JOIN FETCH sb.pet LEFT JOIN FETCH sb.area LEFT JOIN FETCH sb.service WHERE sb.id = :id")
    Optional<ShippingBooking> findDetailedById(@Param("id") Long id);

    @Query("SELECT sb FROM ShippingBooking sb JOIN FETCH sb.user JOIN FETCH sb.pet WHERE sb.id = :id AND sb.user.id = :userId")
    Optional<ShippingBooking> findDetailedOwnedById(@Param("id") Long id, @Param("userId") Long userId);

    List<ShippingBooking> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);

    // New queries for booking flow
    List<ShippingBooking> findByAreaIdAndAppointmentDateAndBookingTypeAndStatusIn(
            Long areaId, LocalDate appointmentDate, String bookingType, List<String> statuses);

    @Query("SELECT sb FROM ShippingBooking sb WHERE sb.area.id = :areaId AND sb.appointmentDate = :date " +
           "AND sb.bookingType = :bookingType AND sb.status IN :statuses " +
           "AND sb.startTime < :endTime AND sb.endTime > :startTime")
    List<ShippingBooking> findOverlappingBookings(
            @Param("areaId") Long areaId,
            @Param("date") LocalDate date,
            @Param("bookingType") String bookingType,
            @Param("statuses") List<String> statuses,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT sb FROM ShippingBooking sb JOIN FETCH sb.user JOIN FETCH sb.pet LEFT JOIN FETCH sb.area LEFT JOIN FETCH sb.service WHERE sb.area.id = :areaId ORDER BY sb.createdAt DESC")
    List<ShippingBooking> findByAreaIdOrderByCreatedAtDesc(@Param("areaId") Long areaId);

    @Query("SELECT sb FROM ShippingBooking sb JOIN FETCH sb.user JOIN FETCH sb.pet LEFT JOIN FETCH sb.area LEFT JOIN FETCH sb.service WHERE sb.status = :status ORDER BY sb.createdAt DESC")
    List<ShippingBooking> findByStatusWithDetails(@Param("status") String status);

    boolean existsByService_Id(Long serviceId);
}
