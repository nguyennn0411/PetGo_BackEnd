package com.example.petgo.controller;

import com.example.petgo.dto.ChatMessageRequest;
import com.example.petgo.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
        private final ChatService chatService;

        @PostMapping("/conversations/direct/provider/{providerId}")
        public ResponseEntity<Map<String, Object>> startProviderChat(HttpServletRequest request,
                        @PathVariable Long providerId) {
                return ResponseEntity.ok(Map.of("message", "Mở chat với provider thành công.", "result",
                                chatService.startProviderChat(request, providerId)));
        }

        @PostMapping("/conversations/support")
        public ResponseEntity<Map<String, Object>> startSupportChat(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of("message", "Mở chat hỗ trợ thành công.", "result",
                                chatService.startSupportChat(request)));
        }

        @PostMapping("/conversations/booking/{bookingId}")
        public ResponseEntity<Map<String, Object>> startBookingChat(HttpServletRequest request,
                        @PathVariable Long bookingId) {
                return ResponseEntity.ok(Map.of("message", "Mở chat booking thành công.", "result",
                                chatService.startBookingChat(request, bookingId)));
        }

        @GetMapping("/conversations")
        public ResponseEntity<Map<String, Object>> listMyConversations(HttpServletRequest request) {
                return ResponseEntity.ok(Map.of("message", "Lấy danh sách chat thành công.", "result",
                                chatService.listMyConversations(request)));
        }

        @GetMapping("/conversations/{conversationId}/messages")
        public ResponseEntity<Map<String, Object>> listMessages(HttpServletRequest request,
                        @PathVariable Long conversationId,
                        @RequestParam(value = "limit", defaultValue = "50") int limit) {
                return ResponseEntity.ok(Map.of("message", "Lấy tin nhắn thành công.", "result",
                                chatService.listMessages(request, conversationId, limit)));
        }

        @PostMapping("/conversations/{conversationId}/messages")
        public ResponseEntity<Map<String, Object>> sendMessage(HttpServletRequest request,
                        @PathVariable Long conversationId,
                        @Valid @RequestBody ChatMessageRequest requestBody) {
                return ResponseEntity.ok(Map.of("message", "Gửi tin nhắn thành công.", "result",
                                chatService.sendMessage(request, conversationId, requestBody)));
        }

        @PostMapping(value = "/conversations/{conversationId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Map<String, Object>> sendImageMessage(HttpServletRequest request,
                        @PathVariable Long conversationId,
                        @RequestParam("file") MultipartFile file) {
                return ResponseEntity.ok(Map.of("message", "Gửi ảnh chat thành công.", "result",
                                chatService.sendImageMessage(request, conversationId, file)));
        }

        @PostMapping("/conversations/{conversationId}/messages/{messageId}/delete")
        public ResponseEntity<Map<String, Object>> deleteMessage(HttpServletRequest request,
                        @PathVariable Long conversationId,
                        @PathVariable Long messageId) {
                return ResponseEntity.ok(Map.of("message", "Xóa tin nhắn thành công.", "result",
                                chatService.deleteMessage(request, conversationId, messageId)));
        }

        @PostMapping("/conversations/{conversationId}/read")
        public ResponseEntity<Map<String, Object>> markAsRead(HttpServletRequest request,
                        @PathVariable Long conversationId) {
                return ResponseEntity.ok(Map.of("message", "Đánh dấu chat đã đọc thành công.", "result",
                                chatService.markAsRead(request, conversationId)));
        }
}