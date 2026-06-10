package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerDashboardSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerDashboardService {
    PartnerDashboardSummaryResponse getSummary(HttpServletRequest request);
}