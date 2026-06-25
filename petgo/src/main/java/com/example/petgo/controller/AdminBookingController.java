package com.example.petgo.controller;

import com.example.petgo.dto.BookingStatusUpdateRequest;
import com.example.petgo.service.AdminBookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings(
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách booking thành công.",
                "result", adminBookingService.getAllBookings(areaId, status, date)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingDetail(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy chi tiết booking thành công.",
                "result", adminBookingService.getBookingDetail(id)));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmBooking(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) BookingStatusUpdateRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", "Xác nhận booking thành công.",
                "result", adminBookingService.confirmBooking(request, id, req)));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeBooking(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) BookingStatusUpdateRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", "Hoàn tất booking thành công.",
                "result", adminBookingService.completeBooking(request, id, req)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) BookingStatusUpdateRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", "Hủy booking thành công.",
                "result", adminBookingService.cancelBooking(request, id, req)));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectBooking(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) BookingStatusUpdateRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", "Từ chối booking thành công.",
                "result", adminBookingService.rejectBooking(request, id, req)));
    }

    @PutMapping("/{id}/note")
    public ResponseEntity<Map<String, Object>> updateNote(
            @PathVariable Long id,
            @RequestBody BookingStatusUpdateRequest req) {
        adminBookingService.updateBookingNote(id, req);
        return ResponseEntity.ok(Map.of("message", "Cập nhật ghi chú thành công."));
    }
}
