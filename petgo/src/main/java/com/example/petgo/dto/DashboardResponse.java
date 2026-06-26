package com.example.petgo.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal totalRevenue,
        BigDecimal serviceRevenue,
        BigDecimal serviceShippingRevenue,
        BigDecimal shopRevenue,
        long totalBookings,
        long totalShopOrders,
        long totalUsers,
        List<DailyRevenue> dailyRevenue) {

    public record DailyRevenue(String date, BigDecimal serviceAmount, BigDecimal shopAmount, BigDecimal total) {
    }
}
