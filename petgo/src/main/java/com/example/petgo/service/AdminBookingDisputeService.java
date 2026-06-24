package com.example.petgo.service;

import com.example.petgo.dto.AdminBookingDisputeResponse;
import com.example.petgo.dto.AdminDisputeResolveRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminBookingDisputeService {

    List<AdminBookingDisputeResponse> getDisputes(HttpServletRequest request);

    AdminBookingDisputeResponse resolveDispute(HttpServletRequest request, Long bookingId,
            AdminDisputeResolveRequest resolveRequest);
}
