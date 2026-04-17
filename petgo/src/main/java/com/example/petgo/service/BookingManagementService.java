package com.example.petgo.service;

import com.example.petgo.dto.*;

public interface BookingManagementService {
    BookingListResponse getMyBookings(Long ownerUserId, String status);
    BookingDetailResponse getBookingDetail(Long ownerUserId, Long bookingId);
    BookingRescheduleContextResponse getRescheduleContext(Long ownerUserId, Long bookingId);
    BookingMutationResponse rescheduleBooking(Long ownerUserId, Long bookingId, BookingRescheduleRequest request);
    BookingMutationResponse cancelBooking(Long ownerUserId, Long bookingId, BookingCancelRequest request);
}
