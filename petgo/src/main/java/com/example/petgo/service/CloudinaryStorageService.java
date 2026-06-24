package com.example.petgo.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryStorageService {

    String uploadPetAvatar(MultipartFile file);

    String uploadChatImage(MultipartFile file);

    String uploadStoreImage(MultipartFile file);

    String uploadPlatformServiceImage(MultipartFile file);

    String uploadUserAvatar(MultipartFile file);

    String uploadUserCover(MultipartFile file);
}