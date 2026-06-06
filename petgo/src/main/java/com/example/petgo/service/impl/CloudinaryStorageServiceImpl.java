package com.example.petgo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageServiceImpl implements CloudinaryStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadPetAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        return uploadImage(file, "petgo/pets/avatar", "File avatar phải là ảnh");
    }

    @Override
    public String uploadPartnerLocationImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ảnh địa điểm nhà cung cấp.");
        }

        return uploadImage(file, "petgo/registrations/partner/locations", "File địa điểm nhà cung cấp phải là ảnh");
    }

    @Override
    public String uploadPartnerServiceImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ảnh mô tả dịch vụ.");
        }

        return uploadImage(file, "petgo/partner/services", "File mô tả dịch vụ phải là ảnh");
    }

    @Override
    public String uploadChatImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ảnh chat.");
        }

        return uploadImage(file, "petgo/chat/images", "File chat phải là ảnh");
    }

    private String uploadImage(MultipartFile file, String folder, String invalidTypeMessage) {
        validateImage(file, invalidTypeMessage);

        try {
            String publicId = UUID.randomUUID().toString().replace("-", "");

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", false));

            Object secureUrl = uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new BadRequestException("Cloudinary không trả về URL ảnh");
            }

            return secureUrl.toString();
        } catch (BadRequestException e) {
            throw e;
        } catch (IOException e) {
            log.warn(
                    "Unable to read image file before Cloudinary upload. folder={}, filename={}, contentType={}, size={}",
                    folder, safeFilename(file), file.getContentType(), file.getSize(), e);
            throw new BadRequestException("Không thể đọc file ảnh");
        } catch (Exception e) {
            log.error("Cloudinary upload failed. folder={}, filename={}, contentType={}, size={}",
                    folder, safeFilename(file), file.getContentType(), file.getSize(), e);
            throw new BadRequestException("Upload ảnh lên Cloudinary thất bại");
        }
    }

    private String safeFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename == null || filename.isBlank() ? "unknown" : filename;
    }

    private void validateImage(MultipartFile file, String invalidTypeMessage) {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException(invalidTypeMessage);
        }

        long maxSize = 5L * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new BadRequestException("Ảnh không được vượt quá 5MB");
        }
    }
}