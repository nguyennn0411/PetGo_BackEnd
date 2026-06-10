package com.example.petgo.service;

import com.example.petgo.dto.NotificationCreateRequest;
import com.example.petgo.dto.NotificationRecipientResponse;
import com.example.petgo.dto.NotificationResponse;
import com.example.petgo.dto.NotificationSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(HttpServletRequest request, NotificationCreateRequest requestBody);

    List<NotificationResponse> listAdminNotifications(HttpServletRequest request);

    List<NotificationRecipientResponse> listMyNotifications(HttpServletRequest request, String status);

    NotificationSummaryResponse getMySummary(HttpServletRequest request);

    NotificationRecipientResponse markAsRead(HttpServletRequest request, Long notificationId);

    NotificationSummaryResponse markAllAsRead(HttpServletRequest request);
}