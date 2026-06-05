package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.NotificationCreateRequest;
import com.example.petgo.dto.NotificationRecipientResponse;
import com.example.petgo.dto.NotificationResponse;
import com.example.petgo.dto.NotificationSummaryResponse;
import com.example.petgo.entity.Notification;
import com.example.petgo.entity.NotificationAudienceType;
import com.example.petgo.entity.NotificationCategory;
import com.example.petgo.entity.NotificationPriority;
import com.example.petgo.entity.NotificationRecipient;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.NotificationRecipientRepository;
import com.example.petgo.repository.NotificationRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Set<RoleType> NOTIFIABLE_ROLE_TYPES = Set.of(RoleType.USER, RoleType.SHOP);

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(HttpServletRequest request, NotificationCreateRequest requestBody) {
        User admin = requireAdmin(request);
        LocalDateTime now = LocalDateTime.now();

        String title = normalizeRequired(requestBody.title(), "Tiêu đề thông báo không được để trống.");
        String content = normalizeRequired(requestBody.content(), "Nội dung thông báo không được để trống.");
        String actionUrl = normalizeActionUrl(requestBody.actionUrl());
        if (requestBody.expiresAt() != null && !requestBody.expiresAt().isAfter(now)) {
            throw new BadRequestException("Thời hạn thông báo phải lớn hơn thời điểm hiện tại.");
        }

        NotificationAudienceType audienceType = requestBody.audienceType() != null
                ? requestBody.audienceType()
                : NotificationAudienceType.INDIVIDUAL;
        List<RoleType> targetRoles = normalizeTargetRoles(requestBody.targetRoles());
        List<User> recipients = resolveRecipients(audienceType, requestBody.recipientUserIds(), targetRoles);
        if (recipients.isEmpty()) {
            throw new BadRequestException("Không tìm thấy người nhận phù hợp để gửi thông báo.");
        }

        Notification notification = new Notification();
        notification.setCreatedBy(admin);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCategory(requestBody.category() != null ? requestBody.category() : NotificationCategory.SYSTEM);
        notification.setPriority(requestBody.priority() != null ? requestBody.priority() : NotificationPriority.NORMAL);
        notification.setAudienceType(audienceType);
        notification.setTargetRoles(buildTargetRolesValue(audienceType, targetRoles));
        notification.setActionUrl(actionUrl);
        notification.setSentAt(now);
        notification.setExpiresAt(requestBody.expiresAt());
        notification = notificationRepository.save(notification);

        Notification savedNotification = notification;
        List<NotificationRecipient> notificationRecipients = recipients.stream()
                .map(recipient -> buildRecipient(savedNotification, recipient, now))
                .toList();
        notificationRecipientRepository.saveAll(notificationRecipients);

        return toAdminResponse(notification, notificationRecipients.size(), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> listAdminNotifications(HttpServletRequest request) {
        requireAdmin(request);
        return notificationRepository.findAllByOrderBySentAtDescIdDesc().stream()
                .map(notification -> {
                    long total = notificationRecipientRepository.countByNotification_Id(notification.getId());
                    long read = notificationRecipientRepository
                            .countByNotification_IdAndReadAtIsNotNull(notification.getId());
                    return toAdminResponse(notification, total, read);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationRecipientResponse> listMyNotifications(HttpServletRequest request, String status) {
        User user = requireCurrentUser(request);
        String normalizedStatus = status == null || status.isBlank() ? "ALL" : status.trim().toUpperCase();
        List<NotificationRecipient> recipients = notificationRecipientRepository
                .findActiveByRecipientId(user.getId(), LocalDateTime.now());

        if ("UNREAD".equals(normalizedStatus)) {
            recipients = recipients.stream().filter(recipient -> recipient.getReadAt() == null).toList();
        } else if ("READ".equals(normalizedStatus)) {
            recipients = recipients.stream().filter(recipient -> recipient.getReadAt() != null).toList();
        } else if (!"ALL".equals(normalizedStatus)) {
            throw new BadRequestException("Bộ lọc trạng thái thông báo không hợp lệ.");
        }

        return recipients.stream().map(this::toRecipientResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationSummaryResponse getMySummary(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        return buildSummary(user.getId());
    }

    @Override
    @Transactional
    public NotificationRecipientResponse markAsRead(HttpServletRequest request, Long notificationId) {
        User user = requireCurrentUser(request);
        NotificationRecipient recipient = notificationRecipientRepository
                .findByNotification_IdAndRecipient_Id(notificationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo của bạn."));
        if (recipient.getReadAt() == null) {
            recipient.setReadAt(LocalDateTime.now());
            recipient = notificationRecipientRepository.save(recipient);
        }
        return toRecipientResponse(recipient);
    }

    @Override
    @Transactional
    public NotificationSummaryResponse markAllAsRead(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        LocalDateTime now = LocalDateTime.now();
        List<NotificationRecipient> unreadRecipients = notificationRecipientRepository
                .findActiveUnreadByRecipientId(user.getId(), now);
        unreadRecipients.forEach(recipient -> recipient.setReadAt(now));
        notificationRecipientRepository.saveAll(unreadRecipients);
        return buildSummary(user.getId());
    }

    private NotificationRecipient buildRecipient(Notification notification, User recipient, LocalDateTime deliveredAt) {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setNotification(notification);
        notificationRecipient.setRecipient(recipient);
        notificationRecipient.setDeliveredAt(deliveredAt);
        return notificationRecipient;
    }

    private User requireAdmin(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(userRole -> userRole.getRole() != null
                        && RoleType.ADMIN.equals(userRole.getRole().getCode()));
        if (!isAdmin) {
            throw new UnauthorizedException("Bạn không có quyền admin.");
        }
        return user;
    }

    private User requireCurrentUser(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        return userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
    }

    private List<User> resolveRecipients(NotificationAudienceType audienceType, List<Long> recipientUserIds,
            List<RoleType> targetRoles) {
        return switch (audienceType) {
            case INDIVIDUAL -> resolveIndividualRecipients(recipientUserIds);
            case ROLE -> resolveRoleRecipients(targetRoles);
            case ALL -> resolveRoleRecipients(List.copyOf(NOTIFIABLE_ROLE_TYPES));
        };
    }

    private List<User> resolveIndividualRecipients(List<Long> recipientUserIds) {
        Set<Long> requestedIds = recipientUserIds == null ? Set.of()
                : recipientUserIds.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        if (requestedIds.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một người nhận.");
        }

        List<User> users = userRepository.findByIdInAndDeletedAtIsNull(requestedIds);
        if (users.size() != requestedIds.size()) {
            throw new BadRequestException("Một số người nhận không tồn tại hoặc đã bị xóa.");
        }
        return deduplicateUsers(users);
    }

    private List<User> resolveRoleRecipients(List<RoleType> targetRoles) {
        List<RoleType> safeRoles = normalizeTargetRoles(targetRoles);
        if (safeRoles.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một role nhận thông báo.");
        }
        return deduplicateUsers(userRoleRepository.findActiveUsersByRoleCodes(safeRoles));
    }

    private List<User> deduplicateUsers(Collection<User> users) {
        Map<Long, User> byId = new LinkedHashMap<>();
        users.stream()
                .filter(user -> user != null && user.getId() != null)
                .forEach(user -> byId.putIfAbsent(user.getId(), user));
        return List.copyOf(byId.values());
    }

    private List<RoleType> normalizeTargetRoles(List<RoleType> targetRoles) {
        if (targetRoles == null) {
            return List.of();
        }
        List<RoleType> roles = targetRoles.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        boolean containsUnsupportedRole = roles.stream().anyMatch(role -> !NOTIFIABLE_ROLE_TYPES.contains(role));
        if (containsUnsupportedRole) {
            throw new BadRequestException("Chỉ hỗ trợ gửi thông báo tới role USER hoặc SHOP.");
        }
        return roles;
    }

    private String buildTargetRolesValue(NotificationAudienceType audienceType, List<RoleType> targetRoles) {
        if (NotificationAudienceType.ALL.equals(audienceType)) {
            return NOTIFIABLE_ROLE_TYPES.stream().map(RoleType::getCode).sorted().collect(Collectors.joining(","));
        }
        if (!NotificationAudienceType.ROLE.equals(audienceType)) {
            return null;
        }
        return targetRoles.stream().map(RoleType::getCode).sorted().collect(Collectors.joining(","));
    }

    private NotificationSummaryResponse buildSummary(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        long total = notificationRecipientRepository.countActiveByRecipientId(userId, now);
        long unread = notificationRecipientRepository.countActiveUnreadByRecipientId(userId, now);
        return NotificationSummaryResponse.builder()
                .total(total)
                .unread(unread)
                .read(Math.max(total - unread, 0))
                .build();
    }

    private NotificationResponse toAdminResponse(Notification notification, long totalRecipients, long readRecipients) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .category(notification.getCategory())
                .priority(notification.getPriority())
                .audienceType(notification.getAudienceType())
                .targetRoles(parseTargetRoles(notification.getTargetRoles()))
                .actionUrl(notification.getActionUrl())
                .sentAt(notification.getSentAt())
                .expiresAt(notification.getExpiresAt())
                .createdById(notification.getCreatedBy() != null ? notification.getCreatedBy().getId() : null)
                .createdByName(notification.getCreatedBy() != null ? notification.getCreatedBy().getFullName() : null)
                .totalRecipients(totalRecipients)
                .readRecipients(readRecipients)
                .unreadRecipients(Math.max(totalRecipients - readRecipients, 0))
                .build();
    }

    private NotificationRecipientResponse toRecipientResponse(NotificationRecipient recipient) {
        Notification notification = recipient.getNotification();
        return NotificationRecipientResponse.builder()
                .id(recipient.getId())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .category(notification.getCategory())
                .priority(notification.getPriority())
                .actionUrl(notification.getActionUrl())
                .sentAt(notification.getSentAt())
                .expiresAt(notification.getExpiresAt())
                .deliveredAt(recipient.getDeliveredAt())
                .readAt(recipient.getReadAt())
                .read(recipient.getReadAt() != null)
                .createdByName(notification.getCreatedBy() != null ? notification.getCreatedBy().getFullName() : null)
                .build();
    }

    private List<String> parseTargetRoles(String targetRoles) {
        if (targetRoles == null || targetRoles.isBlank()) {
            return List.of();
        }
        return Arrays.stream(targetRoles.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String normalizeActionUrl(String actionUrl) {
        String normalized = normalizeNullable(actionUrl);
        if (normalized == null) {
            return null;
        }
        if (!normalized.startsWith("/") && !normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            throw new BadRequestException("Liên kết hành động phải bắt đầu bằng /, http:// hoặc https://.");
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}