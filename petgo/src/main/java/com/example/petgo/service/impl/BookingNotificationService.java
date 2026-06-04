package com.example.petgo.service.impl;

import com.example.petgo.entity.Booking;
import com.example.petgo.entity.Notification;
import com.example.petgo.entity.NotificationAudienceType;
import com.example.petgo.entity.NotificationCategory;
import com.example.petgo.entity.NotificationPriority;
import com.example.petgo.entity.NotificationRecipient;
import com.example.petgo.entity.User;
import com.example.petgo.repository.NotificationRecipientRepository;
import com.example.petgo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingNotificationService {

    private static final DateTimeFormatter VIEW_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter VIEW_TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    public void notifyProviderBookingCreated(Booking booking) {
        User providerUser = booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null;
        createNotification(
                booking != null ? booking.getCustomerUser() : null,
                List.of(providerUser),
                "Booking mới chờ xác nhận",
                String.format("%s đã đặt lịch %s cho %s vào %s. Vui lòng kiểm tra và xác nhận booking.",
                        customerName(booking), serviceName(booking), petName(booking), appointmentText(booking)),
                NotificationPriority.HIGH,
                "/partner/bookings");
    }

    public void notifyOwnerBookingConfirmed(Booking booking) {
        createNotification(
                booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null,
                List.of(booking != null ? booking.getCustomerUser() : null),
                "Lịch đặt đã được xác nhận",
                String.format("%s đã xác nhận lịch %s cho %s vào %s.",
                        providerName(booking), serviceName(booking), petName(booking), appointmentText(booking)),
                NotificationPriority.HIGH,
                booking != null && booking.getId() != null ? "/bookings/" + booking.getId() : "/my-bookings");
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
        notification.setCategory(NotificationCategory.BOOKING);
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

    private String appointmentText(Booking booking) {
        if (booking == null || booking.getAppointmentDate() == null || booking.getStartTime() == null) {
            return "lịch đã chọn";
        }
        String text = booking.getAppointmentDate().format(VIEW_DATE) + " lúc "
                + booking.getStartTime().format(VIEW_TIME);
        if (booking.getEndTime() != null) {
            text += " - " + booking.getEndTime().format(VIEW_TIME);
        }
        return text;
    }

    private String customerName(Booking booking) {
        User customer = booking != null ? booking.getCustomerUser() : null;
        return firstNonBlank(customer != null ? customer.getFullName() : null,
                customer != null ? customer.getEmail() : null,
                "Owner");
    }

    private String providerName(Booking booking) {
        return firstNonBlank(booking != null ? booking.getProviderNameSnapshot() : null,
                booking != null && booking.getProvider() != null ? booking.getProvider().getBusinessName() : null,
                "Nhà cung cấp");
    }

    private String serviceName(Booking booking) {
        return firstNonBlank(booking != null ? booking.getServiceNameSnapshot() : null, "dịch vụ");
    }

    private String petName(Booking booking) {
        return firstNonBlank(booking != null ? booking.getPetNameSnapshot() : null, "thú cưng");
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}