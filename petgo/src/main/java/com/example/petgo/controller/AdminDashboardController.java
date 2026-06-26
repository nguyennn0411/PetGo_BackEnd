package com.example.petgo.controller;

import com.example.petgo.dto.DashboardResponse;
import com.example.petgo.dto.DashboardResponse.DailyRevenue;
import com.example.petgo.repository.ShippingBookingRepository;
import com.example.petgo.repository.ShopOrderRepository;
import com.example.petgo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ShippingBookingRepository bookingRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        BigDecimal serviceRevenue = bookingRepository.sumTotalAmountByStatusCompleted();
        BigDecimal shippingRevenue = bookingRepository.sumShippingFeeByStatusCompleted();
        long totalBookings = bookingRepository.countCompleted();

        BigDecimal shopRevenue = shopOrderRepository.sumTotalAmountByRevenueStatuses();
        long totalShopOrders = shopOrderRepository.countByRevenueStatuses();

        long totalUsers = userRepository.count();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, BigDecimal> serviceByDay = new HashMap<>();
        Map<String, BigDecimal> shopByDay = new HashMap<>();

        for (Object[] row : bookingRepository.dailyServiceRevenueSince(thirtyDaysAgo))
            serviceByDay.put(row[0].toString(), (BigDecimal) row[1]);
        for (Object[] row : shopOrderRepository.dailyShopRevenueSince(thirtyDaysAgo))
            shopByDay.put(row[0].toString(), (BigDecimal) row[1]);

        List<DailyRevenue> dailyList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String key = thirtyDaysAgo.toLocalDate().plusDays(i).format(fmt);
            BigDecimal sv = serviceByDay.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal sh = shopByDay.getOrDefault(key, BigDecimal.ZERO);
            dailyList.add(new DailyRevenue(key, sv, sh, sv.add(sh)));
        }

        BigDecimal totalRevenue = serviceRevenue.add(shopRevenue);

        DashboardResponse data = new DashboardResponse(
                totalRevenue, serviceRevenue, shippingRevenue,
                shopRevenue, totalBookings, totalShopOrders, totalUsers, dailyList);

        return ResponseEntity.ok(Map.of("result", data));
    }
}
