package com.example.petgo.controller;

import com.example.petgo.dto.ProfileUpdateRequest;
import com.example.petgo.service.CloudinaryStorageService;
import com.example.petgo.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final CloudinaryStorageService cloudinaryStorageService;

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

    @PostMapping("/upload-avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryStorageService.uploadUserAvatar(file);
        return ResponseEntity.ok(Map.of(
                "message", "Upload avatar thành công.",
                "result", imageUrl));
    }

    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, Object>> uploadCover(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryStorageService.uploadUserCover(file);
        return ResponseEntity.ok(Map.of(
                "message", "Upload cover thành công.",
                "result", imageUrl));
    }
}
