package com.example.petgo.service;

import com.example.petgo.dto.ChatConversationResponse;
import com.example.petgo.dto.ChatMessageRequest;
import com.example.petgo.dto.ChatMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {
    ChatConversationResponse startProviderChat(HttpServletRequest request, Long providerId);

    ChatConversationResponse startSupportChat(HttpServletRequest request);

    ChatConversationResponse startBookingChat(HttpServletRequest request, Long bookingId);

    List<ChatConversationResponse> listMyConversations(HttpServletRequest request);

    List<ChatMessageResponse> listMessages(HttpServletRequest request, Long conversationId, int limit);

    ChatMessageResponse sendMessage(HttpServletRequest request, Long conversationId, ChatMessageRequest requestBody);

    ChatMessageResponse sendImageMessage(HttpServletRequest request, Long conversationId, MultipartFile file);

    ChatMessageResponse deleteMessage(HttpServletRequest request, Long conversationId, Long messageId);

    ChatConversationResponse markAsRead(HttpServletRequest request, Long conversationId);
}