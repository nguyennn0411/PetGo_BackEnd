package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerDashboardSummaryResponse;
import com.example.petgo.service.partner.PartnerDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner/dashboard")
@RequiredArgsConstructor
public class PartnerDashboardController {

    private final PartnerDashboardService partnerDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<PartnerDashboardSummaryResponse> getSummary(HttpServletRequest request) {
        return ResponseEntity.ok(partnerDashboardService.getSummary(request));
    }
}