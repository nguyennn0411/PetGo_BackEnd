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

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;

    private final Cloudinary cloudinary;

    @Override
    public String uploadPetAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file, "File avatar phải là ảnh");

        try {
            String publicId = UUID.randomUUID().toString().replace("-", "");

            Map uploadResult = cloudinary.uploader().upload(
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
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Upload ảnh lên Cloudinary thất bại");
        }
    }

    @Override
    public String uploadGroomingImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ảnh thú cưng");
        }

        validateImage(file, "File upload phải là ảnh");

        try {
            String publicId = UUID.randomUUID().toString().replace("-", "");

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "petgo/ai-grooming/originals",
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", false
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new BadRequestException("Cloudinary không trả về URL ảnh gốc");
            }

            return secureUrl.toString();
        } catch (IOException e) {
            throw new BadRequestException("Không thể đọc file ảnh thú cưng");
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Upload ảnh grooming lên Cloudinary thất bại");
        }
    }

    @Override
    public String uploadGroomingPreview(byte[] imageBytes, String publicId) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BadRequestException("Dữ liệu ảnh preview không hợp lệ");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "folder", "petgo/ai-grooming/previews",
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", false
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new BadRequestException("Cloudinary không trả về URL ảnh preview");
            }

            return secureUrl.toString();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Upload ảnh preview lên Cloudinary thất bại");
        }
    }

    private void validateImage(MultipartFile file, String invalidTypeMessage) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException(invalidTypeMessage);
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("Ảnh không được vượt quá 5MB");
        }
    }
}
