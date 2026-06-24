package com.example.petgo.controller.partner;

import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.partner.PartnerBookingActionRequest;
import com.example.petgo.dto.partner.PartnerBookingDetailResponse;
import com.example.petgo.dto.partner.PartnerBookingListResponse;
import com.example.petgo.dto.partner.PartnerInternalNoteRequest;
import com.example.petgo.service.partner.PartnerBookingManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partner/bookings")
@RequiredArgsConstructor
public class PartnerBookingController {

    private final PartnerBookingManagementService partnerBookingManagementService;

    @GetMapping
    public ResponseEntity<PartnerBookingListResponse> listBookings(HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Long serviceId) {
        return ResponseEntity.ok(partnerBookingManagementService.listBookings(request, status, from, to, serviceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerBookingDetailResponse> getBookingDetail(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(partnerBookingManagementService.getBookingDetail(request, id));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingMutationResponse> confirmBooking(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.confirmBooking(request, id, requestBody));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingMutationResponse> confirmBookingLegacy(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.confirmBooking(request, id, requestBody));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<BookingMutationResponse> startBooking(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.startBooking(request, id, requestBody));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingMutationResponse> completeBooking(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.completeBooking(request, id, requestBody));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<BookingMutationResponse> rejectBooking(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.rejectBooking(request, id, requestBody));
    }

    @PutMapping("/{id}/confirm-completed-by-provider")
    public ResponseEntity<BookingMutationResponse> confirmCompletedByProvider(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.confirmCompletedByProvider(request, id, requestBody));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingMutationResponse> cancelBooking(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) PartnerBookingActionRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.cancelBooking(request, id, requestBody));
    }

    @PutMapping("/{id}/internal-note")
    public ResponseEntity<PartnerBookingDetailResponse> updateInternalNote(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody PartnerInternalNoteRequest requestBody) {
        return ResponseEntity.ok(partnerBookingManagementService.updateInternalNote(request, id, requestBody));
    }
}