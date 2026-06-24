package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerBookingPolicyRequest;
import com.example.petgo.dto.partner.PartnerBookingPolicyResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerBookingPolicyService {

    PartnerBookingPolicyResponse getPolicy(HttpServletRequest request);

    PartnerBookingPolicyResponse updatePolicy(HttpServletRequest request, PartnerBookingPolicyRequest requestBody);
}