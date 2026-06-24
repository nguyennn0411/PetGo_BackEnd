package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.BookingManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{ownerUserId}/bookings")
@RequiredArgsConstructor
public class BookingManagementController {

    private final BookingManagementService bookingManagementService;

    @GetMapping
    public ResponseEntity<BookingListResponse> getMyBookings(
            @PathVariable Long ownerUserId,
            @RequestParam(required = false, defaultValue = "ALL") String status
    ) {
        return ResponseEntity.ok(bookingManagementService.getMyBookings(ownerUserId, status));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailResponse> getBookingDetail(
            @PathVariable Long ownerUserId,
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(bookingManagementService.getBookingDetail(ownerUserId, bookingId));
    }

    @GetMapping("/{bookingId}/reschedule-context")
    public ResponseEntity<BookingRescheduleContextResponse> getRescheduleContext(
            @PathVariable Long ownerUserId,
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(bookingManagementService.getRescheduleContext(ownerUserId, bookingId));
    }

    @PostMapping("/{bookingId}/reschedule")
    public ResponseEntity<BookingMutationResponse> rescheduleBooking(
            @PathVariable Long ownerUserId,
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingRescheduleRequest request
    ) {
        return ResponseEntity.ok(bookingManagementService.rescheduleBooking(ownerUserId, bookingId, request));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingMutationResponse> cancelBooking(
            @PathVariable Long ownerUserId,
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingCancelRequest request
    ) {
        return ResponseEntity.ok(bookingManagementService.cancelBooking(ownerUserId, bookingId, request));
    }
}
