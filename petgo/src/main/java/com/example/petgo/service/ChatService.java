package com.example.petgo.service;

import com.example.petgo.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ChatService {

    ConversationResponse createConversation(HttpServletRequest request, CreateConversationRequest req);

    List<ConversationResponse> getMyConversations(HttpServletRequest request);

    List<ConversationResponse> getAdminConversations(HttpServletRequest request, String type);

    ConversationResponse getConversationDetail(HttpServletRequest request, Long conversationId);

    List<MessageResponse> getMessages(HttpServletRequest request, Long conversationId);

    MessageResponse sendMessage(HttpServletRequest request, Long conversationId, SendMessageRequest req);

    ConversationResponse updateStatus(HttpServletRequest request, Long conversationId, UpdateConversationStatusRequest req);

    void deleteConversation(HttpServletRequest request, Long conversationId);
}
