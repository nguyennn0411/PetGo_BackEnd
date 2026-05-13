package com.example.petgo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements CloudinaryStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadPetAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file);

        try {
            String publicId = UUID.randomUUID().toString().replace("-", "");

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "petgo/pets/avatar",
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", false
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new BadRequestException("Cloudinary không trả về URL ảnh");
            }

            return secureUrl.toString();
        } catch (IOException e) {
            throw new BadRequestException("Không thể đọc file ảnh");
        } catch (Exception e) {
            throw new BadRequestException("Upload ảnh lên Cloudinary thất bại");
        }
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File avatar phải là ảnh");
        }

        long maxSize = 5L * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new BadRequestException("Ảnh không được vượt quá 5MB");
        }
    }
}