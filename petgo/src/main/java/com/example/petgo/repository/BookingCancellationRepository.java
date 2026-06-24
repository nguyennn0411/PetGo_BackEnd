package com.example.petgo.repository;

import com.example.petgo.entity.BookingCancellation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingCancellationRepository extends JpaRepository<BookingCancellation, Long> {

    @EntityGraph(attributePaths = {"booking", "cancelledByUser"})
    Optional<BookingCancellation> findByBookingId(Long bookingId);
}
