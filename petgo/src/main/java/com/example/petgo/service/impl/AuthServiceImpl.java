package com.example.petgo.service.impl;

import com.example.petgo.dto.request.LoginRequest;
import com.example.petgo.dto.response.AuthResponse;
import com.example.petgo.dto.response.UserResponse;
import com.example.petgo.entity.User;
import com.example.petgo.entity.enums.UserStatus;
import com.example.petgo.exception.AppException;
import com.example.petgo.exception.ErrorCode;
import com.example.petgo.mapper.UserMapper;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    @Transactional // Bỏ readOnly = true vì chúng ta sẽ cập nhật lastLoginAt
    public AuthResponse login(LoginRequest request) {
        String input = request.getUserName();

        // 1. Tìm user
        User user = (input.contains("@")
                ? userRepository.findByEmail(input)
                : userRepository.findByPhoneNumber(input))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "userName"));

        // 2. Kiểm tra trạng thái tài khoản
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Truy cập bị từ chối: User {} có trạng thái {}", input, user.getStatus());
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED, "userName");
            }
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "account_locked_or_inactive");
        }

        // 3. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Mật khẩu không khớp cho user: {}", input);
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "password");
        }

        // 4. Cập nhật thời điểm đăng nhập cuối (Tận dụng trường last_login_at trong DB)
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 5. Tạo bộ đôi Token
        String accessToken = jwtService.generateToken(user, VALID_DURATION);
        String refreshToken = jwtService.generateToken(user, REFRESHABLE_DURATION);

        // 6. Map sang Response (UserMapper sẽ lo các trường địa chỉ, tọa độ...)
        UserResponse userResponse = userMapper.toUserResponse(user);

        log.info("User {} đăng nhập thành công", user.getEmail());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }
}