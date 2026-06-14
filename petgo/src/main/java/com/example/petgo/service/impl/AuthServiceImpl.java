package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.config.JwtTokenService;
import com.example.petgo.dto.AuthLoginRequest;
import com.example.petgo.dto.AuthRegisterRequest;
import com.example.petgo.dto.AuthTokenBundleResponse;
import com.example.petgo.dto.AuthUserResponse;
import com.example.petgo.dto.ForgotPasswordRequest;
import com.example.petgo.dto.ResetPasswordRequest;
import com.example.petgo.entity.RefreshToken;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.User;
import com.example.petgo.entity.UserRole;
import com.example.petgo.entity.UserRoleId;
import com.example.petgo.entity.Wallet;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.RefreshTokenRepository;
import com.example.petgo.repository.RoleRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.repository.WalletRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.MailService;
import com.example.petgo.dto.VerifyOtpRequest;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public AuthUserResponse register(AuthRegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        String normalizedPhone = normalizePhone(request.phoneNumber());

        // Kiểm tra email đã tồn tại chưa
        Optional<User> existingUserOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.getEmailVerifiedAt() != null) {
                // Tài khoản đã được kích hoạt -> Không cho đăng ký
                throw new BadRequestException("Email đã tồn tại.");
            } else if (existingUser.getOtpExpiryTime() != null
                    && existingUser.getOtpExpiryTime().isAfter(LocalDateTime.now())) {
                // Tài khoản chưa xác thực, OTP vẫn còn hạn -> Thông báo chuyển sang xác thực OTP
                throw new BadRequestException("Tài khoản đã được đăng ký. Hệ thống đang chuyển sang trang xác minh OTP.");
            } else {
                // Tài khoản chưa xác thực, OTP đã hết hạn -> Xóa tài khoản cũ và cho tạo mới
                userRoleRepository.deleteByUser(existingUser);
                walletRepository.deleteByUser(existingUser);
                userRepository.delete(existingUser);
                userRepository.flush();
            }
        }

        if (normalizedPhone != null && !normalizedPhone.isBlank()
                && userRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new BadRequestException("Số điện thoại đã tồn tại.");
        }

        User user = new User();
        user.setUserCode(generateUserCode());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        user.setPhoneNumber(normalizedPhone);
        user.setStatus("INACTIVE");
        user.setCountryCode("VN");
        user.setAvatarUrl(defaultAvatarUrl());
        user.setCoverUrl(defaultCoverUrl());

        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);

        mailService.sendOtpEmail(user.getEmail(), otp);

        roleRepository.findByCode(RoleType.USER).ifPresent(role -> {
            UserRole userRole = new UserRole();
            userRole.setId(new UserRoleId(user.getId(), role.getId()));
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        });

        return mapUser(user, resolveRoles(user.getId()));
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizeEmail(request.email()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        if (user.getEmailVerifiedAt() != null) {
            throw new BadRequestException("Email đã được xác thực trước đó.");
        }

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.otpCode())) {
            throw new BadRequestException("Mã OTP không chính xác.");
        }

        if (user.getOtpExpiryTime() == null || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn.");
        }

        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setStatus("ACTIVE");
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizeEmail(request.email()))
                .orElseThrow(() -> new ResourceNotFoundException("Email này chưa tồn tại trong hệ thống."));

        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("Tài khoản của bạn đang bị khóa, không thể đặt lại mật khẩu.");
        }

        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        mailService.sendPasswordResetOtpEmail(user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizeEmail(request.email()))
                .orElseThrow(() -> new ResourceNotFoundException("Email này chưa tồn tại trong hệ thống."));

        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("Tài khoản của bạn đang bị khóa, không thể đặt lại mật khẩu.");
        }

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.otpCode())) {
            throw new BadRequestException("Mã OTP không chính xác.");
        }

        if (user.getOtpExpiryTime() == null || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(LocalDateTime.now());
            user.setStatus("ACTIVE");
        }
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthTokenBundleResponse login(AuthLoginRequest request) {
        String principal = request.userName() == null ? "" : request.userName().trim();
        String password = normalizePassword(request.password());
        User user = findUserForLogin(principal)
                .filter(candidate -> candidate.getDeletedAt() == null)
                .orElseThrow(() -> new UnauthorizedException("Thông tin đăng nhập không chính xác."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Thông tin đăng nhập không chính xác.");
        }

        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Tài khoản của bạn đã bị khóa.");
        }

        if (user.getEmailVerifiedAt() == null) {
            if (user.getOtpExpiryTime() != null && user.getOtpExpiryTime().isAfter(LocalDateTime.now())) {
                // OTP còn hạn (trong 10 phút) -> Thông báo chuyển sang trang xác minh OTP
                throw new UnauthorizedException("Tài khoản đã được đăng ký. Hệ thống đang chuyển sang trang xác minh OTP.");
            } else {
                // OTP đã hết hạn (quá 10 phút) -> Xóa tài khoản và yêu cầu đăng ký lại
                userRoleRepository.deleteByUser(user);
                walletRepository.deleteByUser(user);
                userRepository.delete(user);
                throw new UnauthorizedException("Mã OTP đã hết hạn và tài khoản đã bị hủy. Vui lòng đăng ký lại tài khoản mới.");
            }
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return generateTokenBundle(user);
    }

    @Override
    @Transactional
    public AuthTokenBundleResponse refresh(String authorizationHeader) {
        String rawToken = extractBearerToken(authorizationHeader);
        AuthenticatedUser authenticatedUser = parseTokenOrThrow(rawToken);
        if (!"REFRESH".equalsIgnoreCase(authenticatedUser.tokenType())) {
            throw new UnauthorizedException("Refresh token không hợp lệ.");
        }

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Refresh token không tồn tại hoặc đã bị thu hồi."));

        if (stored.getRevokedAt() != null) {
            throw new UnauthorizedException("Refresh token đã bị thu hồi.");
        }
        if (stored.getExpiresAt() != null && stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token đã hết hạn.");
        }
        if (!stored.getUser().getId().equals(authenticatedUser.userId())) {
            throw new UnauthorizedException("Refresh token không khớp người dùng.");
        }

        stored.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        return generateTokenBundle(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthUserResponse getCurrentUserProfile(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        return mapUser(user, resolveRoles(user.getId()));
    }

    @Override
    @Transactional
    public void logout(String authorizationHeader) {
        String rawToken = extractBearerToken(authorizationHeader);
        String tokenHash = hashToken(rawToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    @Override
    public AuthenticatedUser requireAccessUser(HttpServletRequest request) {
        String rawToken = extractBearerToken(request.getHeader("Authorization"));
        AuthenticatedUser user = parseTokenOrThrow(rawToken);
        if (!"ACCESS".equalsIgnoreCase(user.tokenType())) {
            throw new UnauthorizedException("Access token không hợp lệ.");
        }
        return user;
    }

    private AuthTokenBundleResponse generateTokenBundle(User user) {
        List<String> roles = resolveRoles(user.getId());
        String accessToken = jwtTokenService.generateAccessToken(user, roles);
        String refreshToken = jwtTokenService.generateRefreshToken(user, roles);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(hashToken(refreshToken));
        refreshTokenEntity.setExpiresAt(jwtTokenService.getRefreshTokenExpiry(refreshToken));
        refreshTokenRepository.save(refreshTokenEntity);

        return AuthTokenBundleResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapUser(user, roles))
                .build();
    }

    private Optional<User> findUserForLogin(String principal) {
        if (principal.contains("@")) {
            return userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizeEmail(principal));
        }
        return userRepository.findByPhoneNumberAndDeletedAtIsNull(normalizePhone(principal))
                .or(() -> userRepository.findByUserCodeAndDeletedAtIsNull(principal));
    }

    private List<String> resolveRoles(Long userId) {
        List<String> roles = userRoleRepository.findByUser_Id(userId).stream()
                .map(userRole -> userRole.getRole().getCode())
                .map(RoleType::getCode)
                .filter(code -> code != null && !code.isBlank())
                .distinct()
                .toList();
        return roles.isEmpty() ? List.of(RoleType.USER.getCode()) : roles;
    }

    private AuthUserResponse mapUser(User user, List<String> roles) {
        String address = buildAddress(user);
        return AuthUserResponse.builder()
                .id(user.getId())
                .userId(user.getId())
                .ownerUserId(user.getId())
                .userCode(user.getUserCode())
                .fullName(user.getFullName())
                .name(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(blankToNull(user.getAvatarUrl()) != null ? user.getAvatarUrl() : defaultAvatarUrl())
                .coverUrl(blankToNull(user.getCoverUrl()) != null ? user.getCoverUrl() : defaultCoverUrl())
                .addressLine1(user.getAddressLine1())
                .addressLine2(user.getAddressLine2())
                .ward(user.getWard())
                .district(user.getDistrict())
                .city(user.getCity())
                .province(user.getProvince())
                .countryCode(user.getCountryCode())
                .address(address)
                .status(user.getStatus())
                .createdAt(
                        user.getCreatedAt() != null ? user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                : null)
                .roles(roles)
                .build();
    }

    private String buildAddress(User user) {
        List<String> parts = java.util.stream.Stream.of(
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getWard(),
                user.getDistrict(),
                user.getCity(),
                user.getProvince())
                .filter(value -> value != null && !value.isBlank())
                .toList();
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private String generateUserCode() {
        return "USR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String generateOtp() {
        return String.format("%06d", new java.util.Random().nextInt(1_000_000));
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        return phone == null ? null : phone.trim();
    }

    private String normalizePassword(String password) {
        if (password == null) {
            return null;
        }
        return password.strip();
    }

    private String defaultAvatarUrl() {
        return "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300";
    }

    private String defaultCoverUrl() {
        return "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=1600";
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Thiếu token xác thực.");
        }
        return authorizationHeader.substring(7).trim();
    }

    private AuthenticatedUser parseTokenOrThrow(String rawToken) {
        try {
            return jwtTokenService.parseToken(rawToken);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new UnauthorizedException("Token không hợp lệ hoặc đã hết hạn.");
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không hỗ trợ SHA-256", e);
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
