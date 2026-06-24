package com.example.petgo.service;

import com.example.petgo.dto.AdminBookingDisputeResponse;
import com.example.petgo.dto.AdminDisputeResolveRequest;
import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.ChatConversationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminBookingDisputeService {
    List<AdminBookingDisputeResponse> listDisputes(HttpServletRequest request);

    BookingMutationResponse resolveDispute(HttpServletRequest request, Long bookingId,
            AdminDisputeResolveRequest resolveRequest);

    ChatConversationResponse openDisputeChat(HttpServletRequest request, Long bookingId);
}