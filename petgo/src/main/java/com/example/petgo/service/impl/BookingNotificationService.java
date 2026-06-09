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
import com.example.petgo.service.MailService;
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
    private final MailService mailService;

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
        sendMail(providerUser, "CREATED", booking, "Vui lòng kiểm tra và xác nhận booking mới.");
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
        sendMail(booking != null ? booking.getCustomerUser() : null, "CONFIRMED", booking,
                "Provider đã xác nhận lịch hẹn của bạn.");
    }

    public void notifyOwnerBookingRejected(Booking booking, String reason) {
        User owner = booking != null ? booking.getCustomerUser() : null;
        createNotification(
                booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null,
                List.of(owner),
                "Booking đã bị từ chối",
                String.format("%s đã từ chối lịch %s cho %s. Tiền sẽ được hoàn về ví PetGo.",
                        providerName(booking), serviceName(booking), petName(booking)),
                NotificationPriority.HIGH,
                booking != null && booking.getId() != null ? "/bookings/" + booking.getId() : "/my-bookings");
        sendMail(owner, "REJECTED", booking, reason);
    }

    public void notifyBookingDisputed(Booking booking, String reason) {
        User providerUser = booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null;
        createNotification(
                booking != null ? booking.getCustomerUser() : null,
                List.of(providerUser),
                "Booking đang được khiếu nại",
                String.format("User đã tạo khiếu nại cho booking %s. Admin sẽ tham gia xử lý.",
                        booking != null ? booking.getBookingCode() : ""),
                NotificationPriority.HIGH,
                booking != null && booking.getId() != null ? "/partner/bookings/" + booking.getId() : "/partner/bookings");
        sendMail(providerUser, "DISPUTED", booking, reason);
    }

    public void notifyBookingAdminReview(Booking booking, String reason) {
        User owner = booking != null ? booking.getCustomerUser() : null;
        User providerUser = booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null;
        createNotification(null, List.of(owner, providerUser), "Booking chuyển admin review",
                String.format("Booking %s cần admin xem xét: %s", booking != null ? booking.getBookingCode() : "", reason),
                NotificationPriority.HIGH,
                booking != null && booking.getId() != null ? "/bookings/" + booking.getId() : "/my-bookings");
        sendMail(owner, "ADMIN_REVIEW", booking, reason);
        sendMail(providerUser, "ADMIN_REVIEW", booking, reason);
    }

    public void notifyDisputeResolved(Booking booking, String detail) {
        User owner = booking != null ? booking.getCustomerUser() : null;
        User providerUser = booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null;
        createNotification(null, List.of(owner, providerUser), "Khiếu nại booking đã được xử lý",
                String.format("Admin đã xử lý khiếu nại booking %s.", booking != null ? booking.getBookingCode() : ""),
                NotificationPriority.HIGH,
                booking != null && booking.getId() != null ? "/bookings/" + booking.getId() : "/my-bookings");
        sendMail(owner, "RESOLVED", booking, detail);
        sendMail(providerUser, "RESOLVED", booking, detail);
    }

    public void notifyEscrowReleased(Booking booking, String detail) {
        User providerUser = booking != null && booking.getProvider() != null ? booking.getProvider().getUser() : null;
        createNotification(null, List.of(providerUser), "Escrow booking đã giải ngân",
                String.format("Escrow booking %s đã được chuyển vào ví provider.", booking != null ? booking.getBookingCode() : ""),
                NotificationPriority.NORMAL,
                "/partner/bookings");
        sendMail(providerUser, "ESCROW_RELEASED", booking, detail);
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

    private void sendMail(User recipient, String eventType, Booking booking, String detail) {
        if (recipient != null && recipient.getEmail() != null && !recipient.getEmail().isBlank()) {
            mailService.sendBookingWorkflowEmail(recipient.getEmail(), eventType, booking, detail);
        }
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