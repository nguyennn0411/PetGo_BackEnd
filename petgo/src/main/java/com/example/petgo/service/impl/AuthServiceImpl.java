package com.example.petgo.service.impl;

import com.example.petgo.dto.request.LoginRequest;
import com.example.petgo.dto.response.AuthResponse;
import com.example.petgo.dto.response.UserResponse;
import com.example.petgo.entity.User;
import com.example.petgo.exception.AppException;
import com.example.petgo.exception.ErrorCode;
import com.example.petgo.mapper.UserMapper;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
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


    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String input = request.getUserName();
        Optional<User> userOptional;

        if (input.contains("@")) {
            userOptional = userRepository.findByEmail(input);
        } else {
            userOptional = userRepository.findByPhone(input);         }

        User user = userOptional.orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED, "userName")
        );

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED, "userName");
        }
        System.err.println("=== KIỂM TRA MẬT KHẨU ===");
        System.err.println("DB Hash: " + user.getPasswordHash());
        System.err.println("Input: " + request.getPassword());
        System.err.println("Khớp không: " + passwordEncoder.matches(request.getPassword(), user.getPasswordHash()));
        System.err.println("Mã Hash chuẩn cho mật khẩu 123456 là: " + passwordEncoder.encode("123456"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "userName,password");
        }

        String accessToken = jwtService.generateToken(user, VALID_DURATION);
        String refreshToken = jwtService.generateToken(user, REFRESHABLE_DURATION);

        UserResponse userResponse = userMapper.toUserResponse(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }
}
