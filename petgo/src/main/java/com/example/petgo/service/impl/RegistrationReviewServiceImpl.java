package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.AdminRegistrationReviewRequest;
import com.example.petgo.dto.AdminRegistrationSummaryResponse;
import com.example.petgo.dto.RegistrationResponse;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.RegistrationReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationReviewServiceImpl implements RegistrationReviewService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RegistrationApplicationRepository registrationApplicationRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final RegistrationMapperSupport mapper;
    private final RegistrationNotificationService registrationNotificationService;

    @Override
    @Transactional(readOnly = true)
    public List<AdminRegistrationSummaryResponse> listRegistrations(HttpServletRequest request, RegistrationType type,
            RegistrationStatus status) {
        requireAdmin(request);
        RegistrationType safeType = type != null ? type : RegistrationType.PARTNER;
        List<RegistrationApplication> applications = status == null
                ? registrationApplicationRepository.findByTypeOrderBySubmittedAtDescCreatedAtDesc(safeType)
                : registrationApplicationRepository.findByTypeAndStatusOrderBySubmittedAtDescCreatedAtDesc(safeType,
                        status);
        return applications.stream().map(this::toSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationResponse getRegistrationDetail(HttpServletRequest request, Long id) {
        requireAdmin(request);
        return mapper.toResponse(findApplication(id));
    }

    @Override
    @Transactional
    public RegistrationResponse approve(HttpServletRequest request, Long id,
            AdminRegistrationReviewRequest requestBody) {
        User reviewer = requireAdmin(request);
        RegistrationApplication application = findApplication(id);
        ensureAwaitingApproval(application);

        application.setStatus(RegistrationStatus.APPROVED);
        application.setReviewer(reviewer);
        application.setReviewedAt(LocalDateTime.now());
        application.setAdminMessage(normalizeNullable(requestBody != null ? requestBody.message() : null));
        application.setRejectionReason(null);
        RegistrationApplication savedApplication = registrationApplicationRepository.save(application);

        ensureApprovedRoles(savedApplication.getUser());
        ensureProviderProfile(savedApplication);
        registrationNotificationService.notifyApplicantApproved(savedApplication, savedApplication.getAdminMessage());

        return mapper.toResponse(savedApplication);
    }

    @Override
    @Transactional
    public RegistrationResponse reject(HttpServletRequest request, Long id,
            AdminRegistrationReviewRequest requestBody) {
        User reviewer = requireAdmin(request);
        RegistrationApplication application = findApplication(id);
        ensureAwaitingApproval(application);

        String reason = normalizeRequired(requestBody != null ? requestBody.message() : null,
                "Lý do từ chối không được để trống.");
        application.setStatus(RegistrationStatus.REJECTED);
        application.setReviewer(reviewer);
        application.setReviewedAt(LocalDateTime.now());
        application.setRejectionReason(reason);
        application.setAdminMessage(reason);
        RegistrationApplication savedApplication = registrationApplicationRepository.save(application);
        registrationNotificationService.notifyApplicantRejected(savedApplication, reason);

        return mapper.toResponse(savedApplication);
    }

    @Override
    @Transactional
    public RegistrationResponse requestAdditionalInfo(HttpServletRequest request, Long id,
            AdminRegistrationReviewRequest requestBody) {
        User reviewer = requireAdmin(request);
        RegistrationApplication application = findApplication(id);
        ensureAwaitingApproval(application);

        String message = normalizeRequired(requestBody != null ? requestBody.message() : null,
                "Nội dung yêu cầu bổ sung không được để trống.");
        application.setStatus(RegistrationStatus.NEEDS_MORE_INFO);
        application.setReviewer(reviewer);
        application.setReviewedAt(LocalDateTime.now());
        application.setAdminMessage(message);
        application.setRejectionReason(null);
        RegistrationApplication savedApplication = registrationApplicationRepository.save(application);
        registrationNotificationService.notifyApplicantAdditionalInfoRequested(savedApplication, message);

        return mapper.toResponse(savedApplication);
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(userRole -> userRole.getRole() != null
                        && RoleType.ADMIN.equals(userRole.getRole().getCode()));
        if (!isAdmin) {
            throw new UnauthorizedException("Bạn không có quyền admin.");
        }
        return user;
    }

    private RegistrationApplication findApplication(Long id) {
        return registrationApplicationRepository.findWithUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ đăng ký."));
    }

    private void ensureAwaitingApproval(RegistrationApplication application) {
        if (application.getStatus() != RegistrationStatus.AWAITING_APPROVAL) {
            throw new BadRequestException("Chỉ có thể xét duyệt hồ sơ đang chờ duyệt.");
        }
    }

    private AdminRegistrationSummaryResponse toSummary(RegistrationApplication application) {
        return AdminRegistrationSummaryResponse.builder()
                .id(application.getId())
                .type(application.getType())
                .status(application.getStatus())
                .userId(application.getUser() != null ? application.getUser().getId() : null)
                .userName(application.getUser() != null ? application.getUser().getFullName() : null)
                .userEmail(application.getUser() != null ? application.getUser().getEmail() : null)
                .userPhone(application.getUser() != null ? application.getUser().getPhoneNumber() : null)
                .businessName(application.getBusinessName())
                .businessPhone(application.getBusinessPhone())
                .businessEmail(application.getBusinessEmail())
                .submittedAt(application.getSubmittedAt())
                .reviewedAt(application.getReviewedAt())
                .adminMessage(application.getAdminMessage())
                .rejectionReason(application.getRejectionReason())
                .build();
    }

    private void ensureApprovedRoles(User user) {
        ensureRoleAssigned(user, RoleType.SHOP);
    }

    private void ensureRoleAssigned(User user, RoleType roleType) {
        Role role = roleRepository.findByCode(roleType)
                .orElseGet(() -> createRole(roleType));
        boolean alreadyAssigned = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(
                        userRole -> userRole.getRole() != null && roleType.equals(userRole.getRole().getCode()));
        if (!alreadyAssigned) {
            UserRole userRole = new UserRole();
            userRole.setId(new UserRoleId(user.getId(), role.getId()));
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
    }

    private Role createRole(RoleType roleType) {
        Role role = new Role();
        role.setCode(roleType);
        role.setName(roleType.getDisplayName());
        role.setDescription(roleType.getDescription());
        return roleRepository.save(role);
    }

    private void ensureProviderProfile(RegistrationApplication application) {
        if (providerProfileRepository.findByUser_Id(application.getUser().getId()).isPresent()) {
            return;
        }

        ProviderProfile provider = new ProviderProfile();
        provider.setUser(application.getUser());
        provider.setProviderCode(generateProviderCode());
        provider.setBusinessName(application.getBusinessName());
        provider.setSlug(generateProviderSlug(application.getBusinessName()));
        provider.setProviderType("BUSINESS");
        provider.setDescription(application.getDescription());
        provider.setYearsExperience(null);
        provider.setVerificationStatus("VERIFIED");
        provider.setFeatured(false);
        provider.setHot(false);
        provider.setAcceptsInstantBooking(true);
        provider.setAcceptsMembership(true);
        provider.setAverageRating(BigDecimal.ZERO.setScale(2));
        provider.setTotalReviews(0);
        provider.setTotalCompletedBookings(0);
        provider.setServiceRadiusKm(null);
        provider.setCancellationFreeHours(24);
        provider.setEmergencyPhone(application.getBusinessPhone());
        provider.setPrimaryAddressLine1(application.getBusinessAddress());
        provider.setCountryCode("VN");
        provider.setCurrencyCode("VND");
        provider.setPriceFromAmount(null);
        provider.setStatus("ACTIVE");
        providerProfileRepository.save(provider);
    }

    private String generateProviderCode() {
        return "PRV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
    }

    private String generateProviderSlug(String businessName) {
        String base = businessName == null ? "partner"
                : businessName.trim().toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("(^-|-$)", "");
        if (base.isBlank()) {
            base = "partner";
        }
        String slug;
        do {
            slug = base + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toLowerCase(Locale.ROOT);
        } while (providerProfileRepository.existsBySlug(slug));
        return slug;
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}