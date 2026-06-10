package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerInvoiceDetailResponse;
import com.example.petgo.dto.partner.PartnerInvoiceListResponse;
import com.example.petgo.dto.partner.PartnerRevenueSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerRevenueService {
    PartnerRevenueSummaryResponse getRevenueSummary(HttpServletRequest request, String from, String to);

    PartnerInvoiceListResponse listInvoices(HttpServletRequest request, String from, String to, String status);

    PartnerInvoiceDetailResponse getInvoiceDetail(HttpServletRequest request, Long invoiceId);
}