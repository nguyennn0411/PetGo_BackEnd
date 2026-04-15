package com.example.petgo.service.impl;

import com.example.petgo.dto.request.LoginRequest;
import com.example.petgo.dto.request.RegisterRequest;
import com.example.petgo.dto.response.AuthResponse;
import com.example.petgo.dto.response.UserResponse;
import com.example.petgo.entity.Role;
import com.example.petgo.entity.User;
import com.example.petgo.entity.enums.UserStatus;
import com.example.petgo.exception.AppException;
import com.example.petgo.exception.ErrorCode;
import com.example.petgo.mapper.UserMapper;
import com.example.petgo.repository.RoleRepository;
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
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String input = request.getUserName();

        User user = (input.contains("@")
                ? userRepository.findByEmail(input)
                : userRepository.findByPhoneNumber(input))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "userName"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Truy cập bị từ chối: User {} có trạng thái {}", input, user.getStatus());
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED, "userName");
            }
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "account_locked_or_inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Mật khẩu không khớp cho user: {}", input);
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user, VALID_DURATION);
        String refreshToken = jwtService.generateToken(user, REFRESHABLE_DURATION);

        UserResponse userResponse = userMapper.toUserResponse(user);

        log.info("User {} đăng nhập thành công", user.getEmail());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED, "email");
        }

        Role defaultRole = roleRepository.findById(1L)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Mặc định Role 1 không tồn tại"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .userCode("USR-" + System.currentTimeMillis())
                .status(UserStatus.ACTIVE)
                .countryCode("VN")
                .roles(Collections.singleton(defaultRole)) // Tạo Set chứa 1 phần tử
                .build();

        return userMapper.toUserResponse(userRepository.save(user));
    }
}