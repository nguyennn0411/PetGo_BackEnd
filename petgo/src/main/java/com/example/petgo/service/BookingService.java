package com.example.petgo.service;

import com.example.petgo.dto.BookingCreateContextResponse;
import com.example.petgo.dto.BookingCreateRequest;
import com.example.petgo.dto.BookingSummaryResponse;

public interface BookingService {

    BookingCreateContextResponse getCreateContext(Long ownerUserId,
                                                  Long providerId,
                                                  Long providerServiceId,
                                                  String slotDate,
                                                  String time);

    BookingSummaryResponse createBooking(BookingCreateRequest request);

    BookingSummaryResponse getBookingSummary(Long bookingId);
}
