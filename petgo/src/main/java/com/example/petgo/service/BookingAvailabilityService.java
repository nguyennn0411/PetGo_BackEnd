package com.example.petgo.service;

import com.example.petgo.dto.BookingAvailabilityResponse;
import com.example.petgo.dto.BookingLockRequest;
import com.example.petgo.dto.BookingLockResponse;

import java.time.LocalDate;

public interface BookingAvailabilityService {

    BookingAvailabilityResponse getAvailability(Long providerId, Long providerServiceId, LocalDate date,
            Integer durationMinutes);

    BookingLockResponse createLock(BookingLockRequest request);
}