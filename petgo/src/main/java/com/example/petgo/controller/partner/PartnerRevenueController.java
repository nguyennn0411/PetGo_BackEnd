package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerInvoiceDetailResponse;
import com.example.petgo.dto.partner.PartnerInvoiceListResponse;
import com.example.petgo.dto.partner.PartnerRevenueSummaryResponse;
import com.example.petgo.service.partner.PartnerRevenueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner")
@RequiredArgsConstructor
public class PartnerRevenueController {

    private final PartnerRevenueService partnerRevenueService;

    @GetMapping("/revenue/summary")
    public ResponseEntity<PartnerRevenueSummaryResponse> getRevenueSummary(HttpServletRequest request,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(partnerRevenueService.getRevenueSummary(request, from, to));
    }

    @GetMapping("/invoices")
    public ResponseEntity<PartnerInvoiceListResponse> listInvoices(HttpServletRequest request,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        return ResponseEntity.ok(partnerRevenueService.listInvoices(request, from, to, status));
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<PartnerInvoiceDetailResponse> getInvoiceDetail(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(partnerRevenueService.getInvoiceDetail(request, id));
    }
}