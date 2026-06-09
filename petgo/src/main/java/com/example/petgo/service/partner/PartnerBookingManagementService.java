package com.example.petgo.service.partner;

import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.partner.PartnerBookingActionRequest;
import com.example.petgo.dto.partner.PartnerBookingDetailResponse;
import com.example.petgo.dto.partner.PartnerBookingListResponse;
import com.example.petgo.dto.partner.PartnerInternalNoteRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerBookingManagementService {
        PartnerBookingListResponse listBookings(HttpServletRequest request, String status, String from, String to,
                        Long serviceId);

        PartnerBookingDetailResponse getBookingDetail(HttpServletRequest request, Long bookingId);

        BookingMutationResponse confirmBooking(HttpServletRequest request, Long bookingId,
                        PartnerBookingActionRequest requestBody);

        BookingMutationResponse startBooking(HttpServletRequest request, Long bookingId,
                        PartnerBookingActionRequest requestBody);

        BookingMutationResponse completeBooking(HttpServletRequest request, Long bookingId,
                        PartnerBookingActionRequest requestBody);

        BookingMutationResponse rejectBooking(HttpServletRequest request, Long bookingId,
                        PartnerBookingActionRequest requestBody);

        BookingMutationResponse confirmCompletedByProvider(HttpServletRequest request, Long bookingId,
                        PartnerBookingActionRequest requestBody);

        BookingMutationResponse cancelBooking(HttpServletRequest request, Long bookingId,
                        PartnerBookingActionRequest requestBody);

        PartnerBookingDetailResponse updateInternalNote(HttpServletRequest request, Long bookingId,
                        PartnerInternalNoteRequest requestBody);
}