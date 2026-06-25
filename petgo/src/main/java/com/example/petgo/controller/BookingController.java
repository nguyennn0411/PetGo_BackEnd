package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.AvailabilityService;
import com.example.petgo.service.BookingService;
import com.example.petgo.service.ShippingFeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final AvailabilityService availabilityService;
    private final ShippingFeeService shippingFeeService;
    private final BookingService bookingService;

    @GetMapping("/create-context")
    public ResponseEntity<Map<String, Object>> getCreateContext(
            HttpServletRequest request,
            @RequestParam(required = false) Long areaId) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy thông tin đặt lịch thành công.",
                "result", bookingService.getCreateContext(request, areaId)));
    }

    @GetMapping("/availability/dates")
    public ResponseEntity<Map<String, Object>> getAvailableDates(
            @RequestParam Long areaId,
            @RequestParam Long serviceId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false, defaultValue = "14") Integer days) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy ngày có sẵn thành công.",
                "result", availabilityService.getAvailableDates(areaId, serviceId, from, days)));
    }

    @GetMapping("/availability/slots")
    public ResponseEntity<Map<String, Object>> getAvailableSlots(
            @RequestParam Long areaId,
            @RequestParam Long serviceId,
            @RequestParam String date) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy khung giờ có sẵn thành công.",
                "result", availabilityService.getAvailableSlots(areaId, serviceId, date)));
    }

    @PostMapping("/shipping-fee")
    public ResponseEntity<Map<String, Object>> calculateShippingFee(
            @Valid @RequestBody ShippingFeeRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Tính phí vận chuyển thành công.",
                "result", shippingFeeService.calculateShippingFee(request)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(
            HttpServletRequest request,
            @Valid @RequestBody BookingCreateRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", "Đặt lịch thành công.",
                "result", bookingService.createBooking(request, req)));
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyBookings(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách booking thành công.",
                "result", bookingService.getMyBookings(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMyBookingDetail(
            HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy chi tiết booking thành công.",
                "result", bookingService.getMyBookingDetail(request, id)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", "Hủy booking thành công.",
                "result", bookingService.cancelMyBooking(request, id)));
    }
}
