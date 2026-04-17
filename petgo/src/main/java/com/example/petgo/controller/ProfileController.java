package com.example.petgo.controller;

import com.example.petgo.dto.ProfileUpdateRequest;
import com.example.petgo.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Lấy profile thành công.",
                "result", profileService.getMyProfile(request)
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMyProfile(HttpServletRequest request,
                                                               @Valid @RequestBody ProfileUpdateRequest requestBody) {
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật profile thành công.",
                "result", profileService.updateMyProfile(request, requestBody)
        ));
    }
}
