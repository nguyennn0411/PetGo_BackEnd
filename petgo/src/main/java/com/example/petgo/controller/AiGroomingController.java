package com.example.petgo.controller;

import com.example.petgo.dto.AiGroomingStyleResponse;
import com.example.petgo.service.AiGroomingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai-grooming")
@CrossOrigin(originPatterns = {
        "http://localhost:5173",
        "https://petgo.website",
        "https://*.vercel.app"
})
public class AiGroomingController {
    private final AiGroomingService aiGroomingService;

    public AiGroomingController(AiGroomingService aiGroomingService) {
        this.aiGroomingService = aiGroomingService;
    }

    @PostMapping(value = "/suggestions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AiGroomingStyleResponse>> suggestGroomingStyles(
            @RequestParam("image") MultipartFile image
    ) {
        return ResponseEntity.ok(aiGroomingService.suggestStyles(image));
    }
}
