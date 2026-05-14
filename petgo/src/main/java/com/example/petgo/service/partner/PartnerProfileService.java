package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerProfileResponse;
import com.example.petgo.dto.partner.PartnerProfileUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerProfileService {
    PartnerProfileResponse getProfile(HttpServletRequest request);

    PartnerProfileResponse updateProfile(HttpServletRequest request, PartnerProfileUpdateRequest requestBody);
}