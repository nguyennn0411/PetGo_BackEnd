package com.example.petgo.controller;

import com.example.petgo.dto.NotificationCreateRequest;
import com.example.petgo.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/admin/notifications")
    public ResponseEntity<Map<String, Object>> getAdminNotifications(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách thông báo admin thành công.",
                "result", notificationService.listAdminNotifications(request)));
    }

    @PostMapping("/admin/notifications")
    public ResponseEntity<Map<String, Object>> createNotification(HttpServletRequest request,
            @Valid @RequestBody NotificationCreateRequest requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Tạo và gửi thông báo thành công.",
                "result", notificationService.createNotification(request, requestBody)));
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getMyNotifications(HttpServletRequest request,
            @RequestParam(value = "status", defaultValue = "ALL") String status) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách thông báo thành công.",
                "result", notificationService.listMyNotifications(request, status)));
    }

    @GetMapping("/notifications/summary")
    public ResponseEntity<Map<String, Object>> getMyNotificationSummary(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy tổng quan thông báo thành công.",
                "result", notificationService.getMySummary(request)));
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(HttpServletRequest request,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(Map.of(
                "message", "Đánh dấu thông báo đã đọc thành công.",
                "result", notificationService.markAsRead(request, notificationId)));
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Đánh dấu tất cả thông báo đã đọc thành công.",
                "result", notificationService.markAllAsRead(request)));
    }
}