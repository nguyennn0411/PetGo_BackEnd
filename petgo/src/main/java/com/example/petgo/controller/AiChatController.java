package com.example.petgo.controller;

import com.example.petgo.service.AiChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai-chat")
@CrossOrigin(origins = "http://localhost:5173")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/message")
    public Map<String, String> sendMessage(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String reply = aiChatService.sendMessage(message);
        return Map.of("reply", reply);
    }
}