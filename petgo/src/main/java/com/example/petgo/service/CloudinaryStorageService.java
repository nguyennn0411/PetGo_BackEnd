package com.example.petgo.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryStorageService {

    String uploadPetAvatar(MultipartFile file);

    String uploadPartnerLocationImage(MultipartFile file);

    String uploadPartnerServiceImage(MultipartFile file);

    String uploadChatImage(MultipartFile file);
}