package com.example.petgo.dto.partner;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PartnerInvoiceListResponse(
        Long providerId,
        String filterStatus,
        String fromDate,
        String toDate,
        Map<String, Long> counts,
        List<PartnerInvoiceSummaryResponse> invoices) {
}