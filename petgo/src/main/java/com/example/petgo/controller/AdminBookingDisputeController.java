package com.example.petgo.controller;

import com.example.petgo.dto.AdminBookingDisputeResponse;
import com.example.petgo.dto.AdminDisputeResolveRequest;
import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.ChatConversationResponse;
import com.example.petgo.service.AdminBookingDisputeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/disputes")
@RequiredArgsConstructor
public class AdminBookingDisputeController {
    private final AdminBookingDisputeService adminBookingDisputeService;

    @GetMapping
    public ResponseEntity<List<AdminBookingDisputeResponse>> listDisputes(HttpServletRequest request) {
        return ResponseEntity.ok(adminBookingDisputeService.listDisputes(request));
    }

    @PutMapping("/{bookingId}/resolve")
    public ResponseEntity<BookingMutationResponse> resolveDispute(HttpServletRequest request,
            @PathVariable Long bookingId,
            @Valid @RequestBody AdminDisputeResolveRequest resolveRequest) {
        return ResponseEntity.ok(adminBookingDisputeService.resolveDispute(request, bookingId, resolveRequest));
    }

    @PostMapping("/{bookingId}/chat")
    public ResponseEntity<ChatConversationResponse> openDisputeChat(HttpServletRequest request,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(adminBookingDisputeService.openDisputeChat(request, bookingId));
    }
}