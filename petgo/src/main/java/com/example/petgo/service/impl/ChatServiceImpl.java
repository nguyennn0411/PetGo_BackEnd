package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.ChatConversationResponse;
import com.example.petgo.dto.ChatMessageRequest;
import com.example.petgo.dto.ChatMessageResponse;
import com.example.petgo.dto.ChatParticipantResponse;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.ChatConversation;
import com.example.petgo.entity.ChatMessage;
import com.example.petgo.entity.ChatParticipant;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.ChatConversationRepository;
import com.example.petgo.repository.ChatMessageRepository;
import com.example.petgo.repository.ChatParticipantRepository;
import com.example.petgo.repository.ProviderProfileRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.ChatService;
import com.example.petgo.service.CloudinaryStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private static final String TYPE_DIRECT_PROVIDER = "DIRECT_PROVIDER";
    private static final String TYPE_SUPPORT_DIRECT = "SUPPORT_DIRECT";
    private static final String TYPE_BOOKING_GROUP = "BOOKING_GROUP";
    private static final String STATUS_OPEN = "OPEN";
    private static final String MESSAGE_TYPE_TEXT = "TEXT";
    private static final String MESSAGE_TYPE_IMAGE = "IMAGE";
    private static final String MESSAGE_STATUS_SENT = "SENT";
    private static final int MAX_ACTIVE_IMAGES_PER_CONVERSATION = 10;
    private static final long DELETE_WINDOW_MINUTES = 5;

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final BookingRepository bookingRepository;
    private final ChatConversationRepository chatConversationRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public ChatConversationResponse startProviderChat(HttpServletRequest request, Long providerId) {
        User user = requireCurrentUser(request);
        ProviderProfile provider = providerProfileRepository.findActiveById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy provider đang hoạt động."));
        if (provider.getUser() == null) {
            throw new BadRequestException("Provider chưa có tài khoản owner để chat.");
        }
        if (provider.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không thể tự chat với provider của chính mình.");
        }

        ChatConversation conversation = chatConversationRepository
                .findDirectProviderConversation(user.getId(), provider.getId())
                .orElseGet(() -> {
                    ChatConversation created = createConversation(TYPE_DIRECT_PROVIDER,
                            "Chat với " + provider.getBusinessName(), provider, null, user);
                    addParticipant(created, user, "CUSTOMER");
                    addParticipant(created, provider.getUser(), "PROVIDER");
                    return created;
                });
        return toConversationResponse(conversation);
    }

    @Override
    @Transactional
    public ChatConversationResponse startSupportChat(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        ChatConversation conversation = chatConversationRepository.findSupportConversation(user.getId())
                .orElseGet(() -> {
                    ChatConversation created = createConversation(TYPE_SUPPORT_DIRECT, "PetGo Support", null, null,
                            user);
                    addParticipant(created, user, "CUSTOMER");
                    return created;
                });
        return toConversationResponse(conversation);
    }

    @Override
    @Transactional
    public ChatConversationResponse startBookingChat(HttpServletRequest request, Long bookingId) {
        User user = requireCurrentUser(request);
        Booking booking = bookingRepository.findDetailedById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
        ensureBookingChatAccess(user, booking);

        ChatConversation conversation = chatConversationRepository.findBookingConversation(booking.getId())
                .orElseGet(() -> createBookingConversation(booking, user));
        return toConversationResponse(conversation);
    }

    @Override
    @Transactional
    public ChatConversationResponse ensureAdminBookingDisputeChat(Long bookingId) {
        Booking booking = bookingRepository.findDetailedById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
        ChatConversation conversation = chatConversationRepository.findBookingConversation(booking.getId())
                .orElseGet(() -> createBookingConversation(booking, booking.getCustomerUser()));
        userRoleRepository.findActiveUsersByRoleCodes(List.of(RoleType.ADMIN))
                .forEach(admin -> addParticipant(conversation, admin, "ADMIN"));
        conversation.setStatus(STATUS_OPEN);
        conversation.setLastMessagePreview("Admin đã được thêm vào chat dispute booking.");
        conversation.setLastMessageAt(LocalDateTime.now());
        return toConversationResponse(chatConversationRepository.save(conversation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversationResponse> listMyConversations(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        return chatConversationRepository.findMyConversations(user.getId()).stream()
                .map(this::toConversationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> listMessages(HttpServletRequest request, Long conversationId, int limit) {
        User user = requireCurrentUser(request);
        ensureParticipant(conversationId, user.getId());
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return chatMessageRepository
                .findByConversation_IdAndDeletedAtIsNullOrderByIdDesc(conversationId, PageRequest.of(0, safeLimit))
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getId))
                .map(message -> toMessageResponse(message, user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(HttpServletRequest request, Long conversationId,
            ChatMessageRequest requestBody) {
        User user = requireCurrentUser(request);
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy conversation."));
        ensureParticipant(conversationId, user.getId());
        String content = normalizeRequired(requestBody.content(), "Nội dung tin nhắn không được để trống.");
        LocalDateTime now = LocalDateTime.now();

        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(user);
        message.setMessageType(MESSAGE_TYPE_TEXT);
        message.setContent(content);
        message.setStatus(MESSAGE_STATUS_SENT);
        message = chatMessageRepository.save(message);

        conversation.setLastMessagePreview(content.length() > 255 ? content.substring(0, 255) : content);
        conversation.setLastMessageAt(now);
        chatConversationRepository.save(conversation);
        markParticipantRead(conversationId, user.getId(), message);
        return toMessageResponse(message, user.getId());
    }

    @Override
    @Transactional
    public ChatMessageResponse sendImageMessage(HttpServletRequest request, Long conversationId, MultipartFile file) {
        User user = requireCurrentUser(request);
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy conversation."));
        ensureParticipant(conversationId, user.getId());

        String imageUrl = cloudinaryStorageService.uploadChatImage(file);
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(user);
        message.setMessageType(MESSAGE_TYPE_IMAGE);
        message.setContent("Đã gửi một ảnh");
        message.setAttachmentUrl(imageUrl);
        message.setStatus(MESSAGE_STATUS_SENT);
        message = chatMessageRepository.save(message);

        conversation.setLastMessagePreview("[Ảnh]");
        conversation.setLastMessageAt(LocalDateTime.now());
        chatConversationRepository.save(conversation);
        markParticipantRead(conversationId, user.getId(), message);
        enforceActiveImageLimit(conversationId);
        return toMessageResponse(message, user.getId());
    }

    @Override
    @Transactional
    public ChatMessageResponse deleteMessage(HttpServletRequest request, Long conversationId, Long messageId) {
        User user = requireCurrentUser(request);
        ensureParticipant(conversationId, user.getId());
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin nhắn."));
        if (message.getConversation() == null || !message.getConversation().getId().equals(conversationId)) {
            throw new ResourceNotFoundException("Tin nhắn không thuộc conversation này.");
        }
        if (message.getDeletedAt() != null) {
            return toMessageResponse(message, user.getId());
        }
        if (message.getSender() == null || !message.getSender().getId().equals(user.getId())) {
            throw new UnauthorizedException("Bạn chỉ có thể xóa tin nhắn của chính mình.");
        }
        LocalDateTime createdAt = message.getCreatedAt();
        if (createdAt != null
                && Duration.between(createdAt, LocalDateTime.now()).toMinutes() >= DELETE_WINDOW_MINUTES) {
            throw new BadRequestException("Chỉ có thể xóa tin nhắn trong vòng 5 phút sau khi gửi.");
        }
        message.setDeletedAt(LocalDateTime.now());
        message.setStatus("DELETED");
        return toMessageResponse(chatMessageRepository.save(message), user.getId());
    }

    @Override
    @Transactional
    public ChatConversationResponse markAsRead(HttpServletRequest request, Long conversationId) {
        User user = requireCurrentUser(request);
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy conversation."));
        ensureParticipant(conversationId, user.getId());
        chatMessageRepository.findByConversation_IdAndDeletedAtIsNullOrderByIdDesc(conversationId, PageRequest.of(0, 1))
                .stream().findFirst().ifPresent(message -> markParticipantRead(conversationId, user.getId(), message));
        return toConversationResponse(conversation);
    }

    private ChatConversation createConversation(String type, String title, ProviderProfile provider, Booking booking,
            User createdBy) {
        ChatConversation conversation = new ChatConversation();
        conversation.setType(type);
        conversation.setStatus(STATUS_OPEN);
        conversation.setTitle(title);
        conversation.setProvider(provider);
        conversation.setBooking(booking);
        conversation.setCreatedBy(createdBy);
        return chatConversationRepository.save(conversation);
    }

    private ChatConversation createBookingConversation(Booking booking, User createdBy) {
        ChatConversation created = createConversation(TYPE_BOOKING_GROUP,
                "Booking " + booking.getBookingCode(), booking.getProvider(), booking, createdBy);
        addParticipant(created, booking.getCustomerUser(), "CUSTOMER");
        if (booking.getProvider() != null && booking.getProvider().getUser() != null) {
            addParticipant(created, booking.getProvider().getUser(), "PROVIDER");
        }
        return created;
    }

    private void addParticipant(ChatConversation conversation, User user, String roleInChat) {
        if (user == null || chatParticipantRepository
                .existsByConversation_IdAndUser_IdAndLeftAtIsNull(conversation.getId(), user.getId())) {
            return;
        }
        ChatParticipant participant = new ChatParticipant();
        participant.setConversation(conversation);
        participant.setUser(user);
        participant.setRoleInChat(roleInChat);
        participant.setJoinedAt(LocalDateTime.now());
        chatParticipantRepository.save(participant);
    }

    private void markParticipantRead(Long conversationId, Long userId, ChatMessage message) {
        chatParticipantRepository.findByConversation_IdAndUser_IdAndLeftAtIsNull(conversationId, userId)
                .ifPresent(participant -> {
                    participant.setLastReadMessage(message);
                    chatParticipantRepository.save(participant);
                });
    }

    private void ensureParticipant(Long conversationId, Long userId) {
        if (!chatParticipantRepository.existsByConversation_IdAndUser_IdAndLeftAtIsNull(conversationId, userId)) {
            throw new UnauthorizedException("Bạn không có quyền truy cập conversation này.");
        }
    }

    private void ensureBookingChatAccess(User user, Booking booking) {
        boolean isCustomer = booking.getCustomerUser() != null
                && booking.getCustomerUser().getId().equals(user.getId());
        boolean isProviderOwner = booking.getProvider() != null && booking.getProvider().getUser() != null
                && booking.getProvider().getUser().getId().equals(user.getId());
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(userRole -> userRole.getRole() != null && userRole.getRole().getCode() == RoleType.ADMIN);
        if (!isCustomer && !isProviderOwner && !isAdmin) {
            throw new UnauthorizedException("Bạn không có quyền mở chat booking này.");
        }
    }

    private ChatConversationResponse toConversationResponse(ChatConversation conversation) {
        List<ChatParticipantResponse> participants = chatParticipantRepository
                .findByConversation_IdAndLeftAtIsNullOrderByIdAsc(conversation.getId())
                .stream()
                .map(this::toParticipantResponse)
                .toList();
        return ChatConversationResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .status(conversation.getStatus())
                .title(conversation.getTitle())
                .providerId(conversation.getProvider() != null ? conversation.getProvider().getId() : null)
                .providerName(conversation.getProvider() != null ? conversation.getProvider().getBusinessName() : null)
                .bookingId(conversation.getBooking() != null ? conversation.getBooking().getId() : null)
                .bookingCode(conversation.getBooking() != null ? conversation.getBooking().getBookingCode() : null)
                .lastMessagePreview(conversation.getLastMessagePreview())
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .participants(participants)
                .build();
    }

    private ChatParticipantResponse toParticipantResponse(ChatParticipant participant) {
        User user = participant.getUser();
        return ChatParticipantResponse.builder()
                .userId(user != null ? user.getId() : null)
                .fullName(user != null ? user.getFullName() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .roleInChat(participant.getRoleInChat())
                .joinedAt(participant.getJoinedAt())
                .build();
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        return toMessageResponse(message, null);
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message, Long currentUserId) {
        User sender = message.getSender();
        return ChatMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation() != null ? message.getConversation().getId() : null)
                .senderId(sender != null ? sender.getId() : null)
                .senderName(sender != null ? sender.getFullName() : null)
                .senderAvatarUrl(sender != null ? sender.getAvatarUrl() : null)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .attachmentUrl(message.getAttachmentUrl())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .canDelete(canDelete(message, currentUserId))
                .build();
    }

    private void enforceActiveImageLimit(Long conversationId) {
        List<ChatMessage> images = chatMessageRepository.findActiveImagesDesc(conversationId);
        if (images.size() <= MAX_ACTIVE_IMAGES_PER_CONVERSATION) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        images.stream().skip(MAX_ACTIVE_IMAGES_PER_CONVERSATION).forEach(message -> {
            message.setDeletedAt(now);
            message.setStatus("DELETED");
            chatMessageRepository.save(message);
        });
    }

    private boolean canDelete(ChatMessage message, Long currentUserId) {
        if (message == null || currentUserId == null || message.getDeletedAt() != null || message.getSender() == null
                || !message.getSender().getId().equals(currentUserId) || message.getCreatedAt() == null) {
            return false;
        }
        return Duration.between(message.getCreatedAt(), LocalDateTime.now()).toMinutes() < DELETE_WINDOW_MINUTES;
    }

    private User requireCurrentUser(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        return userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new UnauthorizedException("Không tìm thấy người dùng hiện tại."));
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }
}