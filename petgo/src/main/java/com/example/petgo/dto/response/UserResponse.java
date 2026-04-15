package com.example.petgo.dto.response;

import com.example.petgo.entity.enums.Gender;
import com.example.petgo.entity.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String userCode;
    String fullName;
    String email;
    String phoneNumber;
    String avatarUrl;
    String coverUrl;
    LocalDate dateOfBirth;
    Gender gender;

    // --- PHẦN ĐỊA CHỈ ĐẦY ĐỦ ---
    String addressLine1;
    String addressLine2;
    String ward;
    String district;
    String city;
    String province;
    String countryCode;

    // --- TỌA ĐỘ ---
    BigDecimal latitude;
    BigDecimal longitude;

    // --- TRẠNG THÁI & METADATA ---
    UserStatus status;
    LocalDateTime emailVerifiedAt;
    LocalDateTime phoneVerifiedAt;
    LocalDateTime lastLoginAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // --- DANH SÁCH QUYỀN (Trả về danh sách String code: VD ["USER", "ADMIN"]) ---
    Set<String> roles;
}