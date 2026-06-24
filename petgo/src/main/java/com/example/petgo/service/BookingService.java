package com.example.petgo.service;

import com.example.petgo.dto.BookingCreateContextResponse;
import com.example.petgo.dto.BookingCreateRequest;
import com.example.petgo.dto.BookingDisputeRequest;
import com.example.petgo.dto.BookingSummaryResponse;
import com.example.petgo.dto.BookingAvailabilityResponse;
import com.example.petgo.dto.BookingMutationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

public interface BookingService {

        BookingCreateContextResponse getCreateContext(Long ownerUserId,
                        Long providerId,
                        Long providerServiceId,
                        String slotDate,
                        String time);

        BookingCreateContextResponse getCreateContext(HttpServletRequest request,
                        Long providerId,
                        Long providerServiceId,
                        String slotDate,
                        String time);

        BookingAvailabilityResponse getAvailableDates(Long providerId, Long providerServiceId, LocalDate from,
                        Integer days);

        BookingAvailabilityResponse getAvailableSlots(Long providerId, Long providerServiceId, LocalDate date);

        BookingSummaryResponse createBooking(BookingCreateRequest request);

        BookingSummaryResponse createBooking(HttpServletRequest request, BookingCreateRequest createRequest);

        BookingMutationResponse confirmCompletedByUser(HttpServletRequest request, Long bookingId);

        BookingMutationResponse createDispute(HttpServletRequest request, Long bookingId,
                        BookingDisputeRequest disputeRequest);

        BookingSummaryResponse getBookingSummary(Long bookingId);
}
