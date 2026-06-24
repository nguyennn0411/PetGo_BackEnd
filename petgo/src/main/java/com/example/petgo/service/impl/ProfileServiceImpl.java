package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.AuthUserResponse;
import com.example.petgo.dto.ProfileResponse;
import com.example.petgo.dto.ProfileUpdateRequest;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.PetRepository;
import com.example.petgo.repository.ReviewRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        return buildProfileResponse(user, request);
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(HttpServletRequest request, ProfileUpdateRequest requestBody) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        String nextEmail = requestBody.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(nextEmail, user.getId())) {
            throw new BadRequestException("Email đã được sử dụng bởi tài khoản khác.");
        }

        String nextPhone = normalizeNullable(requestBody.phoneNumber());
        if (nextPhone != null && !nextPhone.isBlank() && userRepository.existsByPhoneNumberAndIdNot(nextPhone, user.getId())) {
            throw new BadRequestException("Số điện thoại đã được sử dụng bởi tài khoản khác.");
        }

        user.setFullName(requestBody.fullName().trim());
        user.setEmail(nextEmail);
        user.setPhoneNumber(nextPhone);
        user.setAvatarUrl(normalizeNullable(requestBody.avatarUrl()));
        user.setCoverUrl(normalizeNullable(requestBody.coverUrl()));
        user.setAddressLine1(normalizeNullable(requestBody.addressLine1()));
        user.setAddressLine2(normalizeNullable(requestBody.addressLine2()));
        user.setWard(normalizeNullable(requestBody.ward()));
        user.setDistrict(normalizeNullable(requestBody.district()));
        user.setCity(normalizeNullable(requestBody.city()));
        user.setProvince(normalizeNullable(requestBody.province()));

        userRepository.save(user);
        return buildProfileResponse(user, request);
    }

    private ProfileResponse buildProfileResponse(User user, HttpServletRequest request) {
        AuthUserResponse authUserResponse = authService.getCurrentUserProfile(request);
        long totalPets = petRepository.countActiveByOwnerUserId(user.getId());
        long totalBookings = bookingRepository.countByCustomerUser_Id(user.getId());
        long totalReviews = reviewRepository.countByCustomerUser_IdAndDeletedAtIsNull(user.getId());

        return ProfileResponse.builder()
                .user(authUserResponse)
                .totalPets(totalPets)
                .totalBookings(totalBookings)
                .totalReviews(totalReviews)
                .build();
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
