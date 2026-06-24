package com.example.petgo.controller;

import com.example.petgo.dto.BookingCreateContextResponse;
import com.example.petgo.dto.BookingCreateRequest;
import com.example.petgo.dto.BookingDisputeRequest;
import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.BookingSummaryResponse;
import com.example.petgo.dto.BookingAvailabilityResponse;
import com.example.petgo.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/create-context")
    public ResponseEntity<BookingCreateContextResponse> getCreateContext(
            HttpServletRequest request,
            @RequestParam Long providerId,
            @RequestParam(required = false) Long providerServiceId,
            @RequestParam(required = false) String slotDate,
            @RequestParam(required = false) String time) {
        return ResponseEntity
                .ok(bookingService.getCreateContext(request, providerId, providerServiceId, slotDate, time));
    }

    @GetMapping("/availability/dates")
    public ResponseEntity<BookingAvailabilityResponse> getAvailableDates(
            @RequestParam Long providerId,
            @RequestParam Long providerServiceId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false, defaultValue = "14") Integer days) {
        return ResponseEntity.ok(bookingService.getAvailableDates(providerId, providerServiceId, from, days));
    }

    @GetMapping("/availability/slots")
    public ResponseEntity<BookingAvailabilityResponse> getAvailableSlots(
            @RequestParam Long providerId,
            @RequestParam Long providerServiceId,
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(providerId, providerServiceId, date));
    }

    @PostMapping
    public ResponseEntity<BookingSummaryResponse> createBooking(HttpServletRequest request,
            @Valid @RequestBody BookingCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request, createRequest));
    }

    @PutMapping("/{bookingId}/confirm-completed-by-user")
    public ResponseEntity<BookingMutationResponse> confirmCompletedByUser(HttpServletRequest request,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.confirmCompletedByUser(request, bookingId));
    }

    @PostMapping("/{bookingId}/disputes")
    public ResponseEntity<BookingMutationResponse> createDispute(HttpServletRequest request,
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingDisputeRequest disputeRequest) {
        return ResponseEntity.ok(bookingService.createDispute(request, bookingId, disputeRequest));
    }

    @GetMapping("/{bookingId}/summary")
    public ResponseEntity<BookingSummaryResponse> getBookingSummary(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingSummary(bookingId));
    }
}
