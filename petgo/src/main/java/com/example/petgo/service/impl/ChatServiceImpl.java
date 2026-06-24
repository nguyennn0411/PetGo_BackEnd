package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public ConversationResponse createConversation(HttpServletRequest request, CreateConversationRequest req) {
        User user = requireUser(request);
        ConversationType type = parseType(req.type());

        Conversation conv = new Conversation();
        conv.setUser(user);
        conv.setType(type);
        conv.setStatus("OPEN");
        conv.setTitle(req.title().trim());
        conversationRepository.save(conv);

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSender(user);
        msg.setContent(req.content().trim());
        if (req.imageUrl() != null && !req.imageUrl().isBlank())
            msg.setImageUrl(req.imageUrl().trim());
        if (req.errorCode() != null && !req.errorCode().isBlank())
            msg.setErrorCode(req.errorCode().trim());
        msg.setIsSystemMessage(false);
        messageRepository.save(msg);

        return mapConversation(conv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations(HttpServletRequest request) {
        User user = requireUser(request);
        return conversationRepository.findByUserIdAndDeletedAtIsNullOrderByUpdatedAtDesc(user.getId())
                .stream().map(this::mapConversation).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getAdminConversations(HttpServletRequest request, String type) {
        requireAdmin(request);
        if (type != null && !type.isBlank()) {
            ConversationType convType = parseType(type);
            return conversationRepository.findByTypeAndDeletedAtIsNullOrderByUpdatedAtDesc(convType)
                    .stream().map(this::mapConversation).toList();
        }
        return conversationRepository.findAllByDeletedAtIsNullOrderByUpdatedAtDesc()
                .stream().map(this::mapConversation).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversationDetail(HttpServletRequest request, Long conversationId) {
        Conversation conv = findActiveConversation(request, conversationId);
        return mapConversation(conv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(HttpServletRequest request, Long conversationId) {
        Conversation conv = findActiveConversation(request, conversationId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId())
                .stream().map(this::mapMessage).toList();
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(HttpServletRequest request, Long conversationId, SendMessageRequest req) {
        User sender = requireUser(request);
        Conversation conv = conversationRepository.findActiveById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội thoại."));
        boolean isAdmin = isAdmin(sender.getId());

        if (!isAdmin && !conv.getUser().getId().equals(sender.getId()))
            throw new UnauthorizedException("Bạn không có quyền gửi tin nhắn trong hội thoại này.");

        if (!isAdmin && "COMPLETED".equalsIgnoreCase(conv.getStatus()))
            throw new BadRequestException("Hội thoại đã kết thúc, bạn không thể gửi tin nhắn.");

        if (!isAdmin && "DELETED".equalsIgnoreCase(conv.getStatus()))
            throw new BadRequestException("Hội thoại đã bị xóa.");

        if (conv.getDeletedAt() != null)
            throw new ResourceNotFoundException("Không tìm thấy hội thoại.");

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setContent(req.content().trim());
        if (req.imageUrl() != null && !req.imageUrl().isBlank())
            msg.setImageUrl(req.imageUrl().trim());
        if (req.errorCode() != null && !req.errorCode().isBlank())
            msg.setErrorCode(req.errorCode().trim());
        msg.setIsSystemMessage(false);
        messageRepository.save(msg);

        conv.setUpdatedAt(LocalDateTime.now(APP_ZONE));
        conversationRepository.save(conv);

        return mapMessage(msg);
    }

    @Override
    @Transactional
    public ConversationResponse updateStatus(HttpServletRequest request, Long conversationId,
            UpdateConversationStatusRequest req) {
        User admin = requireAdmin(request);
        Conversation conv = conversationRepository.findActiveById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội thoại."));

        String status = req.status().trim().toUpperCase();
        if (!List.of("OPEN", "PROCESSING", "COMPLETED").contains(status))
            throw new BadRequestException("Trạng thái không hợp lệ. Chấp nhận: OPEN, PROCESSING, COMPLETED.");

        String oldStatus = conv.getStatus();
        conv.setStatus(status);
        conversationRepository.save(conv);

        Message systemMsg = new Message();
        systemMsg.setConversation(conv);
        systemMsg.setSender(admin);
        systemMsg.setIsSystemMessage(true);
        systemMsg.setContent("Trạng thái hội thoại đã chuyển từ " + statusLabel(oldStatus) + " sang " + statusLabel(status));
        messageRepository.save(systemMsg);

        return mapConversation(conv);
    }

    @Override
    @Transactional
    public void deleteConversation(HttpServletRequest request, Long conversationId) {
        requireAdmin(request);
        Conversation conv = conversationRepository.findActiveById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội thoại."));
        conv.setDeletedAt(LocalDateTime.now(APP_ZONE));
        conversationRepository.save(conv);
    }

    private Conversation findActiveConversation(HttpServletRequest request, Long conversationId) {
        User user = requireUser(request);
        Conversation conv = conversationRepository.findActiveById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội thoại."));
        boolean isAdmin = isAdmin(user.getId());
        if (!isAdmin && !conv.getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("Bạn không có quyền xem hội thoại này.");
        return conv;
    }

    private ConversationResponse mapConversation(Conversation conv) {
        int msgCount = messageRepository.countByConversationId(conv.getId());
        MessageResponse lastMsg = messageRepository.findLastByConversationId(conv.getId())
                .map(this::mapMessage).orElse(null);
        return new ConversationResponse(
                conv.getId(),
                conv.getUser().getId(),
                conv.getUser().getFullName(),
                conv.getType().getCode(),
                conv.getType().getLabel(),
                conv.getStatus(),
                statusLabel(conv.getStatus()),
                conv.getTitle(),
                msgCount,
                lastMsg,
                conv.getCreatedAt(),
                conv.getUpdatedAt());
    }

    private MessageResponse mapMessage(Message msg) {
        return new MessageResponse(
                msg.getId(),
                msg.getConversation().getId(),
                msg.getSender().getId(),
                msg.getSender().getFullName(),
                msg.getContent(),
                msg.getImageUrl(),
                msg.getErrorCode(),
                Boolean.TRUE.equals(msg.getIsSystemMessage()),
                msg.getCreatedAt());
    }

    private User requireUser(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        return userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser current = authService.requireAccessUser(request);
        User user = userRepository.findById(current.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        if (!isAdmin(user.getId()))
            throw new UnauthorizedException("Bạn không có quyền admin.");
        return user;
    }

    private boolean isAdmin(Long userId) {
        return userRoleRepository.findByUser_Id(userId).stream()
                .anyMatch(ur -> ur.getRole() != null && ur.getRole().getCode() != null
                        && "ADMIN".equalsIgnoreCase(ur.getRole().getCode().getCode()));
    }

    private ConversationType parseType(String type) {
        if (type == null) throw new BadRequestException("Loại hội thoại không được để trống.");
        return switch (type.trim().toUpperCase()) {
            case "REPORT" -> ConversationType.REPORT;
            case "QA" -> ConversationType.QA;
            default -> throw new BadRequestException("Loại hội thoại không hợp lệ. Chấp nhận: REPORT, QA.");
        };
    }

    private String statusLabel(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "OPEN" -> "Mở";
            case "PROCESSING" -> "Đang xử lý";
            case "COMPLETED" -> "Hoàn thành";
            default -> status;
        };
    }
}
