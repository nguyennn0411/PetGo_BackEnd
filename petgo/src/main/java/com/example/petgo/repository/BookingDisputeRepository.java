package com.example.petgo.repository;

import com.example.petgo.entity.BookingDispute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingDisputeRepository extends JpaRepository<BookingDispute, Long> {

    @Query("SELECT bd FROM BookingDispute bd JOIN FETCH bd.booking b JOIN FETCH b.user WHERE bd.status = :status ORDER BY bd.createdAt DESC")
    List<BookingDispute> findByStatusWithBooking(@Param("status") String status);

    @Query("SELECT bd FROM BookingDispute bd JOIN FETCH bd.booking b JOIN FETCH b.user WHERE b.id = :bookingId")
    Optional<BookingDispute> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT bd FROM BookingDispute bd JOIN FETCH bd.booking b JOIN FETCH b.user WHERE bd.id = :id")
    Optional<BookingDispute> findDetailedById(@Param("id") Long id);
}
