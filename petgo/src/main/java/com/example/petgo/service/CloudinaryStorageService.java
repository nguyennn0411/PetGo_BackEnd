package com.example.petgo.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryStorageService {
    String uploadPetAvatar(MultipartFile file);

    String uploadGroomingImage(MultipartFile file);

    String uploadGroomingPreview(byte[] imageBytes, String publicId);
}
