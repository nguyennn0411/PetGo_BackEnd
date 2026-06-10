package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerRevenueSummaryResponse(
        Long providerId,
        String businessName,
        String fromDate,
        String toDate,
        BigDecimal totalRevenue,
        String totalRevenueDisplay,
        BigDecimal pendingRevenue,
        String pendingRevenueDisplay,
        BigDecimal averageBookingValue,
        String averageBookingValueDisplay,
        long invoiceCount,
        long paidInvoices,
        long pendingInvoices,
        long totalBookings,
        long completedBookings,
        List<PartnerRevenueByServiceResponse> revenueByService,
        List<PartnerInvoiceSummaryResponse> recentInvoices) {
}