package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.RegistrationAdditionalInfoRequest;
import com.example.petgo.dto.RegistrationResponse;
import com.example.petgo.dto.RegistrationSubmitRequest;
import com.example.petgo.dto.RegistrationUpsertRequest;
import com.example.petgo.entity.RegistrationApplication;
import com.example.petgo.entity.RegistrationStatus;
import com.example.petgo.entity.RegistrationType;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.RegistrationApplicationRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private static final int MIN_LOCATION_IMAGE_COUNT = 4;
    private static final int MAX_LOCATION_IMAGE_COUNT = 10;

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RegistrationApplicationRepository registrationApplicationRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final RegistrationMapperSupport mapper;
    private final RegistrationNotificationService registrationNotificationService;

    @Override
    @Transactional(readOnly = true)
    public RegistrationResponse getMyPartnerRegistration(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        return registrationApplicationRepository.findByUser_IdAndType(user.getId(), RegistrationType.PARTNER)
                .map(mapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public RegistrationResponse savePartnerDraft(HttpServletRequest request, RegistrationUpsertRequest requestBody) {
        User user = requireCurrentUser(request);
        RegistrationApplication application = registrationApplicationRepository
                .findByUser_IdAndType(user.getId(), RegistrationType.PARTNER)
                .orElseGet(() -> newDraft(user));

        if (application.getStatus() == RegistrationStatus.APPROVED
                || application.getStatus() == RegistrationStatus.AWAITING_APPROVAL) {
            throw new BadRequestException("Không thể chỉnh sửa hồ sơ ở trạng thái hiện tại.");
        }
        if (application.getStatus() == RegistrationStatus.REJECTED) {
            prepareRejectedApplicationForReapply(application);
        }

        applyBusinessFields(application, requestBody);
        registrationApplicationRepository.save(application);
        return mapper.toResponse(application);
    }

    @Override
    @Transactional
    public RegistrationResponse submitPartnerRegistration(HttpServletRequest request,
            RegistrationSubmitRequest requestBody) {
        User user = requireCurrentUser(request);
        RegistrationApplication application = registrationApplicationRepository
                .findByUser_IdAndType(user.getId(), RegistrationType.PARTNER)
                .orElseGet(() -> newDraft(user));

        if (application.getStatus() == RegistrationStatus.APPROVED) {
            throw new BadRequestException("Hồ sơ đã được duyệt, không thể gửi lại.");
        }
        if (application.getStatus() == RegistrationStatus.AWAITING_APPROVAL) {
            throw new BadRequestException("Hồ sơ đang chờ admin xét duyệt.");
        }
        if (application.getStatus() == RegistrationStatus.REJECTED) {
            prepareRejectedApplicationForReapply(application);
        }

        if (requestBody != null && requestBody.application() != null) {
            applyBusinessFields(application, requestBody.application());
        }
        validateReadyToSubmit(application);

        application.setStatus(RegistrationStatus.AWAITING_APPROVAL);
        application.setSubmittedAt(LocalDateTime.now());
        application.setReviewedAt(null);
        application.setReviewer(null);
        application.setAdminMessage(null);
        application.setRejectionReason(null);

        RegistrationApplication savedApplication = registrationApplicationRepository.save(application);
        registrationNotificationService.notifyAdminsPartnerSubmitted(savedApplication);
        return mapper.toResponse(savedApplication);
    }

    @Override
    @Transactional
    public RegistrationResponse submitPartnerAdditionalInformation(HttpServletRequest request,
            RegistrationAdditionalInfoRequest requestBody) {
        User user = requireCurrentUser(request);
        RegistrationApplication application = registrationApplicationRepository
                .findByUser_IdAndType(user.getId(), RegistrationType.PARTNER)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ đăng ký partner."));

        if (application.getStatus() != RegistrationStatus.NEEDS_MORE_INFO) {
            throw new BadRequestException("Chỉ có thể bổ sung thông tin khi admin yêu cầu.");
        }

        if (requestBody != null && requestBody.application() != null) {
            applyBusinessFields(application, requestBody.application());
        }
        application.setAdditionalInformation(
                normalizeRequired(requestBody != null ? requestBody.additionalInformation() : null,
                        "Thông tin bổ sung không được để trống."));
        validateReadyToSubmit(application);
        application.setStatus(RegistrationStatus.AWAITING_APPROVAL);
        application.setSubmittedAt(LocalDateTime.now());
        application.setReviewedAt(null);
        application.setReviewer(null);
        application.setRejectionReason(null);

        RegistrationApplication savedApplication = registrationApplicationRepository.save(application);
        registrationNotificationService.notifyAdminsPartnerAdditionalInfoSubmitted(savedApplication);
        return mapper.toResponse(savedApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getPartnerRegistrationHistory(HttpServletRequest request) {
        User user = requireCurrentUser(request);
        RegistrationApplication application = registrationApplicationRepository
                .findByUser_IdAndType(user.getId(), RegistrationType.PARTNER)
                .orElse(null);
        return mapper.toHistoryList(application);
    }

    private User requireCurrentUser(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        return userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
    }

    private RegistrationApplication newDraft(User user) {
        RegistrationApplication application = new RegistrationApplication();
        application.setUser(user);
        application.setType(RegistrationType.PARTNER);
        application.setStatus(RegistrationStatus.DRAFT);
        application.setBusinessName("");
        application.setBusinessPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        application.setBusinessEmail(user.getEmail() != null ? user.getEmail() : "");
        application.setBusinessAddress(buildDefaultAddress(user));
        application.setRepresentativeName(user.getFullName() != null ? user.getFullName() : "");
        application.setRepresentativePhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        application.setRepresentativeEmail(user.getEmail() != null ? user.getEmail() : "");
        return application;
    }

    private void prepareRejectedApplicationForReapply(RegistrationApplication application) {
        application.setStatus(RegistrationStatus.DRAFT);
        application.setSubmittedAt(null);
        application.setReviewedAt(null);
        application.setReviewer(null);
        application.setAdminMessage(null);
        application.setRejectionReason(null);
        application.setAdditionalInformation(null);
    }

    private void applyBusinessFields(RegistrationApplication application, RegistrationUpsertRequest requestBody) {
        application.setBusinessName(
                normalizeRequired(requestBody.businessName(), "Tên nhà cung cấp/doanh nghiệp không được để trống."));
        application.setBusinessPhone(
                normalizeRequired(requestBody.businessPhone(), "Số điện thoại nhà cung cấp không được để trống."));
        application.setBusinessEmail(
                normalizeRequired(requestBody.businessEmail(), "Email nhà cung cấp không được để trống.")
                        .toLowerCase());
        application.setBusinessAddress(
                normalizeRequired(requestBody.businessAddress(), "Địa chỉ nhà cung cấp không được để trống."));
        application.setTaxCode(normalizeNullable(requestBody.taxCode()));
        application.setRepresentativeName(
                normalizeRequired(requestBody.representativeName(), "Tên người đại diện không được để trống."));
        application.setRepresentativePhone(normalizeRequired(requestBody.representativePhone(),
                "Số điện thoại người đại diện không được để trống."));
        application.setRepresentativeEmail(
                normalizeRequired(requestBody.representativeEmail(), "Email người đại diện không được để trống.")
                        .toLowerCase());
        application.setDescription(normalizeNullable(requestBody.description()));
        validateServiceCategoryIds(requestBody.serviceCategoryIds());
        application.setServiceCategoryIds(mapper.toCsv(requestBody.serviceCategoryIds()));
        application.setLocationImageUrls(mapper.toTextLines(requestBody.locationImageUrls()));
    }

    private void validateReadyToSubmit(RegistrationApplication application) {
        normalizeRequired(application.getBusinessName(), "Tên nhà cung cấp/doanh nghiệp không được để trống.");
        normalizeRequired(application.getBusinessPhone(), "Số điện thoại nhà cung cấp không được để trống.");
        normalizeRequired(application.getBusinessEmail(), "Email nhà cung cấp không được để trống.");
        normalizeRequired(application.getBusinessAddress(), "Địa chỉ nhà cung cấp không được để trống.");
        normalizeRequired(application.getRepresentativeName(), "Tên người đại diện không được để trống.");
        normalizeRequired(application.getRepresentativePhone(), "Số điện thoại người đại diện không được để trống.");
        normalizeRequired(application.getRepresentativeEmail(), "Email người đại diện không được để trống.");

        List<Long> categoryIds = mapper.parseLongCsv(application.getServiceCategoryIds());
        if (categoryIds.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một nhóm dịch vụ nhà cung cấp cung cấp.");
        }
        validateServiceCategoryIds(categoryIds);

        List<String> imageUrls = mapper.parseTextLines(application.getLocationImageUrls());
        if (imageUrls.size() < MIN_LOCATION_IMAGE_COUNT) {
            throw new BadRequestException("Vui lòng cung cấp tối thiểu 4 ảnh về địa điểm nhà cung cấp.");
        }
        if (imageUrls.size() > MAX_LOCATION_IMAGE_COUNT) {
            throw new BadRequestException("Chỉ được cung cấp tối đa 10 ảnh về địa điểm nhà cung cấp.");
        }
    }

    private void validateServiceCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        List<Long> normalizedIds = categoryIds.stream()
                .filter(id -> id != null)
                .distinct()
                .toList();
        long existingCount = serviceCategoryRepository.findAllById(normalizedIds).stream()
                .filter(category -> Boolean.TRUE.equals(category.getActive()))
                .count();
        long requestedCount = normalizedIds.size();
        if (existingCount != requestedCount) {
            throw new BadRequestException("Nhóm dịch vụ không hợp lệ hoặc đã ngừng hoạt động.");
        }
    }

    private String buildDefaultAddress(User user) {
        return java.util.stream.Stream
                .of(user.getAddressLine1(), user.getAddressLine2(), user.getWard(), user.getDistrict(), user.getCity(),
                        user.getProvince())
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
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