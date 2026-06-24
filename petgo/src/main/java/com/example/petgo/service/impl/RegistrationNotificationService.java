package com.example.petgo.service.impl;

import com.example.petgo.entity.Notification;
import com.example.petgo.entity.NotificationAudienceType;
import com.example.petgo.entity.NotificationCategory;
import com.example.petgo.entity.NotificationPriority;
import com.example.petgo.entity.NotificationRecipient;
import com.example.petgo.entity.RegistrationApplication;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.User;
import com.example.petgo.repository.NotificationRecipientRepository;
import com.example.petgo.repository.NotificationRepository;
import com.example.petgo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationNotificationService {

    private static final String ADMIN_PARTNER_REVIEW_URL = "/admin/partners";
    private static final String PARTNER_REGISTRATION_URL = "/partner-registration/provider";
    private static final String PARTNER_DASHBOARD_URL = "/partner/dashboard";

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final UserRoleRepository userRoleRepository;

    public void notifyAdminsPartnerSubmitted(RegistrationApplication application) {
        createNotification(
                application.getUser(),
                findAdmins(),
                "Hồ sơ mới chờ duyệt",
                String.format("%s đã gửi hồ sơ đăng ký và đang chờ xét duyệt.", applicantName(application)),
                NotificationPriority.HIGH,
                ADMIN_PARTNER_REVIEW_URL);
    }

    public void notifyAdminsPartnerAdditionalInfoSubmitted(RegistrationApplication application) {
        String additionalInfo = normalizeNullable(application.getAdditionalInformation());
        createNotification(
                application.getUser(),
                findAdmins(),
                "Partner đã bổ sung thông tin",
                String.format("%s đã bổ sung thông tin cho hồ sơ đăng ký.%s",
                        applicantName(application),
                        additionalInfo == null ? "" : "\n\nNội dung bổ sung: " + truncate(additionalInfo, 700)),
                NotificationPriority.HIGH,
                ADMIN_PARTNER_REVIEW_URL);
    }

    public void notifyApplicantAdditionalInfoRequested(RegistrationApplication application, String message) {
        createNotification(
                application.getReviewer(),
                List.of(application.getUser()),
                "Cần bổ sung hồ sơ",
                String.format("Bạn cần bổ sung thông tin để hồ sơ được tiếp tục xét duyệt.\n\nYêu cầu: %s",
                        truncate(message, 1200)),
                NotificationPriority.HIGH,
                PARTNER_REGISTRATION_URL);
    }

    public void notifyApplicantApproved(RegistrationApplication application, String message) {
        String note = normalizeNullable(message);
        createNotification(
                application.getReviewer(),
                List.of(application.getUser()),
                "Hồ sơ đã được duyệt",
                String.format(
                        "Hồ sơ của bạn đã được duyệt. Bạn có thể vào Partner Dashboard để vận hành nhà cung cấp.%s",
                        note == null ? "" : "\n\nGhi chú admin: " + truncate(note, 700)),
                NotificationPriority.HIGH,
                PARTNER_DASHBOARD_URL);
    }

    public void notifyApplicantRejected(RegistrationApplication application, String reason) {
        createNotification(
                application.getReviewer(),
                List.of(application.getUser()),
                "Hồ sơ bị từ chối",
                String.format("Hồ sơ của bạn đã bị từ chối.\n\nLý do: %s",
                        truncate(reason, 1200)),
                NotificationPriority.HIGH,
                PARTNER_REGISTRATION_URL);
    }

    private List<User> findAdmins() {
        return userRoleRepository.findActiveUsersByRoleCodes(List.of(RoleType.ADMIN));
    }

    private void createNotification(User actor,
            List<User> recipients,
            String title,
            String content,
            NotificationPriority priority,
            String actionUrl) {
        List<User> safeRecipients = deduplicateRecipients(recipients);
        if (safeRecipients.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Notification notification = new Notification();
        notification.setCreatedBy(actor);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCategory(NotificationCategory.PARTNER);
        notification.setPriority(priority != null ? priority : NotificationPriority.NORMAL);
        notification.setAudienceType(NotificationAudienceType.INDIVIDUAL);
        notification.setTargetRoles(null);
        notification.setActionUrl(actionUrl);
        notification.setSentAt(now);
        notification.setExpiresAt(null);
        notification = notificationRepository.save(notification);

        Notification savedNotification = notification;
        notificationRecipientRepository.saveAll(safeRecipients.stream()
                .map(recipient -> toRecipient(savedNotification, recipient, now))
                .toList());
    }

    private NotificationRecipient toRecipient(Notification notification, User recipient, LocalDateTime deliveredAt) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(recipient);
        notificationRecipient.setDeliveredAt(deliveredAt);
        return notificationRecipient;
    }

    private List<User> deduplicateRecipients(List<User> recipients) {
        if (recipients == null || recipients.isEmpty()) {
            return List.of();
        }

        Map<Long, User> byId = new LinkedHashMap<>();
        recipients.stream()
                .filter(user -> user != null && user.getId() != null)
                .forEach(user -> byId.putIfAbsent(user.getId(), user));
        return List.copyOf(byId.values());
    }

    private String applicantName(RegistrationApplication application) {
        User user = application.getUser();
        if (user == null) {
            return "Người dùng";
        }
        String displayName = normalizeNullable(user.getFullName());
        if (displayName != null) {
            return displayName;
        }
        displayName = normalizeNullable(user.getEmail());
        return displayName != null ? displayName : "Người dùng #" + user.getId();
    }

    private String businessName(RegistrationApplication application) {
        String name = normalizeNullable(application.getBusinessName());
        return name != null ? "“" + name + "”" : "của bạn";
    }

    private String truncate(String value, int maxLength) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            return "Không có nội dung.";
        }
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(maxLength - 3, 0)) + "...";
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}