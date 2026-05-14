package com.example.petgo.service;

import com.example.petgo.dto.AdminRegistrationReviewRequest;
import com.example.petgo.dto.AdminRegistrationSummaryResponse;
import com.example.petgo.dto.RegistrationResponse;
import com.example.petgo.entity.RegistrationStatus;
import com.example.petgo.entity.RegistrationType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RegistrationReviewService {
    List<AdminRegistrationSummaryResponse> listRegistrations(HttpServletRequest request, RegistrationType type, RegistrationStatus status);
    RegistrationResponse getRegistrationDetail(HttpServletRequest request, Long id);
    RegistrationResponse approve(HttpServletRequest request, Long id, AdminRegistrationReviewRequest requestBody);
    RegistrationResponse reject(HttpServletRequest request, Long id, AdminRegistrationReviewRequest requestBody);
    RegistrationResponse requestAdditionalInfo(HttpServletRequest request, Long id, AdminRegistrationReviewRequest requestBody);
}