package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/conversations")
    public ResponseEntity<Map<String, Object>> createConversation(HttpServletRequest request,
            @Valid @RequestBody CreateConversationRequest req) {
        return ResponseEntity.ok(Map.of("message", "Tạo hội thoại thành công.",
                "result", chatService.createConversation(request, req)));
    }

    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> getMyConversations(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("message", "Lấy danh sách hội thoại thành công.",
                "result", chatService.getMyConversations(request)));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<Map<String, Object>> getConversationDetail(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Lấy chi tiết hội thoại thành công.",
                "result", chatService.getConversationDetail(request, id)));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<Map<String, Object>> getMessages(HttpServletRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Lấy tin nhắn thành công.",
                "result", chatService.getMessages(request, id)));
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(Map.of("message", "Gửi tin nhắn thành công.",
                "result", chatService.sendMessage(request, id, req)));
    }

    @PatchMapping("/admin/conversations/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody UpdateConversationStatusRequest req) {
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái hội thoại thành công.",
                "result", chatService.updateStatus(request, id, req)));
    }

    @GetMapping("/admin/conversations")
    public ResponseEntity<Map<String, Object>> getAdminConversations(HttpServletRequest request,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(Map.of("message", "Lấy danh sách hội thoại thành công.",
                "result", chatService.getAdminConversations(request, type)));
    }

    @DeleteMapping("/admin/conversations/{id}")
    public ResponseEntity<Map<String, Object>> deleteConversation(HttpServletRequest request,
            @PathVariable Long id) {
        chatService.deleteConversation(request, id);
        return ResponseEntity.ok(Map.of("message", "Xóa hội thoại thành công."));
    }
}
