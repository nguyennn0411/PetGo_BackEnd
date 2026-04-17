package com.example.petgo.controller;

import com.example.petgo.dto.BookingCreateContextResponse;
import com.example.petgo.dto.BookingCreateRequest;
import com.example.petgo.dto.BookingSummaryResponse;
import com.example.petgo.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/create-context")
    public ResponseEntity<BookingCreateContextResponse> getCreateContext(
            @RequestParam Long ownerUserId,
            @RequestParam Long providerId,
            @RequestParam(required = false) Long providerServiceId,
            @RequestParam(required = false) String slotDate,
            @RequestParam(required = false) String time
    ) {
        return ResponseEntity.ok(bookingService.getCreateContext(ownerUserId, providerId, providerServiceId, slotDate, time));
    }

    @PostMapping
    public ResponseEntity<BookingSummaryResponse> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @GetMapping("/{bookingId}/summary")
    public ResponseEntity<BookingSummaryResponse> getBookingSummary(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingSummary(bookingId));
    }
}
