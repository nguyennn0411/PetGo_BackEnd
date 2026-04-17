package com.example.petgo.repository;

import com.example.petgo.entity.BookingReschedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRescheduleRepository extends JpaRepository<BookingReschedule, Long> {
    List<BookingReschedule> findByBookingIdOrderByCreatedAtDescIdDesc(Long bookingId);
}
