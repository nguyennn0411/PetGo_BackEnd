package com.example.petgo.service.partner.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.partner.AdminPartnerServiceReviewRequest;
import com.example.petgo.dto.partner.PartnerServiceCategoryResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeItemResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestUpsertRequest;
import com.example.petgo.entity.CatalogService;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.entity.ProviderServiceChangeRequest;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.CatalogServiceRepository;
import com.example.petgo.repository.ProviderServiceChangeRequestRepository;
import com.example.petgo.repository.ProviderServiceRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerServiceChangeRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerServiceChangeRequestServiceImpl implements PartnerServiceChangeRequestService {

    private static final String TYPE_CREATE = "CREATE";
    private static final String TYPE_UPDATE = "UPDATE";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PENDING = "PENDING_REVIEW";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final int MAX_DRAFTS_PER_PROVIDER = 3;
    private static final int MIN_PHOTOS = 1;
    private static final int MAX_PHOTOS = 5;
    private static final String DEFAULT_DURATION_TYPE = "FIXED";

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderServiceChangeRequestRepository changeRequestRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PartnerServiceCategoryResponse> listCategoryOptions(HttpServletRequest request) {
        partnerAccessService.requirePartnerContext(request);
        return serviceCategoryRepository.findByActiveTrueOrderByNameAscIdAsc().stream()
                .map(this::mapCategory)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerServiceChangeRequestResponse> listMyRequests(HttpServletRequest request) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return changeRequestRepository.findDetailedByProviderId(provider.getId()).stream()
                .map(this::mapRequest)
                .toList();
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse saveDraft(HttpServletRequest request,
            PartnerServiceChangeRequestUpsertRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ensureDraftLimit(provider.getId(), null);
        ProviderServiceChangeRequest draft = new ProviderServiceChangeRequest();
        draft.setProvider(provider);
        draft.setStatus(STATUS_DRAFT);
        applyDraftFields(provider, draft, requestBody);
        return mapRequest(changeRequestRepository.save(draft));
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse updateDraft(HttpServletRequest request, Long requestId,
            PartnerServiceChangeRequestUpsertRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderServiceChangeRequest draft = requireOwnedRequest(provider.getId(), requestId);
        ensureDraft(draft);
        applyDraftFields(provider, draft, requestBody);
        return mapRequest(changeRequestRepository.save(draft));
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse submitNewRequest(HttpServletRequest request,
            PartnerServiceChangeRequestUpsertRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderServiceChangeRequest changeRequest = new ProviderServiceChangeRequest();
        changeRequest.setProvider(provider);
        changeRequest.setStatus(STATUS_DRAFT);
        applyDraftFields(provider, changeRequest, requestBody);
        submit(changeRequest);
        return mapRequest(changeRequestRepository.save(changeRequest));
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse submitDraft(HttpServletRequest request, Long requestId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderServiceChangeRequest draft = requireOwnedRequest(provider.getId(), requestId);
        ensureDraft(draft);
        submit(draft);
        return mapRequest(changeRequestRepository.save(draft));
    }

    @Override
    @Transactional
    public void deleteDraft(HttpServletRequest request, Long requestId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderServiceChangeRequest draft = requireOwnedRequest(provider.getId(), requestId);
        ensureDraft(draft);
        changeRequestRepository.delete(draft);
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse copyFromService(HttpServletRequest request, Long providerServiceId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ensureDraftLimit(provider.getId(), null);
        ProviderService service = requireOwnedService(provider.getId(), providerServiceId);
        ProviderServiceChangeRequest draft = new ProviderServiceChangeRequest();
        draft.setProvider(provider);
        draft.setStatus(STATUS_DRAFT);
        draft.setRequestType(TYPE_CREATE);
        draft.setServiceName(mapper.firstNonBlank(service.getCustomName(),
                service.getService() != null ? service.getService().getName() : null));
        draft.setPhotoUrls(service.getPhotoUrls());
        draft.setPriceAmount(service.getPriceAmount());
        draft.setCurrencyCode(mapper.firstNonBlank(service.getCurrencyCode(), "VND"));
        draft.setPriceUnit(mapper.firstNonBlank(service.getPriceUnit(), "SESSION"));
        draft.setDescription(service.getDescription());
        return mapRequest(changeRequestRepository.save(draft));
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse copyFromRequest(HttpServletRequest request, Long requestId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ensureDraftLimit(provider.getId(), null);
        ProviderServiceChangeRequest source = requireOwnedRequest(provider.getId(), requestId);
        ProviderServiceChangeRequest draft = new ProviderServiceChangeRequest();
        draft.setProvider(provider);
        draft.setStatus(STATUS_DRAFT);
        draft.setRequestType(TYPE_CREATE);
        draft.setServiceName(source.getServiceName());
        draft.setPhotoUrls(source.getPhotoUrls());
        draft.setPriceAmount(source.getPriceAmount());
        draft.setCurrencyCode(mapper.firstNonBlank(source.getCurrencyCode(), "VND"));
        draft.setPriceUnit(mapper.firstNonBlank(source.getPriceUnit(), "SESSION"));
        draft.setDescription(source.getDescription());
        return mapRequest(changeRequestRepository.save(draft));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerServiceChangeRequestResponse> listAdminRequests(HttpServletRequest request, String status) {
        requireAdmin(request);
        String normalizedStatus = normalizeStatusFilter(status);
        return changeRequestRepository.findDetailedForAdmin(normalizedStatus).stream()
                .map(this::mapRequest)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerServiceChangeRequestResponse getAdminRequestDetail(HttpServletRequest request, Long requestId) {
        requireAdmin(request);
        return mapRequest(requireRequest(requestId));
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse approve(HttpServletRequest request, Long requestId,
            AdminPartnerServiceReviewRequest requestBody) {
        User reviewer = requireAdmin(request);
        ProviderServiceChangeRequest changeRequest = requireRequest(requestId);
        ensurePending(changeRequest);
        List<Long> reviewCategoryIds = normalizeReviewCategoryIds(requestBody);
        changeRequest.setCategoryIds(toCsv(reviewCategoryIds));

        ProviderService providerService = TYPE_UPDATE.equalsIgnoreCase(changeRequest.getRequestType())
                ? applyApprovedUpdate(changeRequest)
                : applyApprovedCreate(changeRequest);
        changeRequest.setProviderService(providerService);
        changeRequest.setStatus(STATUS_APPROVED);
        changeRequest.setReviewer(reviewer);
        changeRequest.setReviewedAt(LocalDateTime.now());
        changeRequest.setAdminMessage(mapper.normalizeBlank(requestBody != null ? requestBody.message() : null));
        return mapRequest(changeRequestRepository.save(changeRequest));
    }

    @Override
    @Transactional
    public PartnerServiceChangeRequestResponse reject(HttpServletRequest request, Long requestId,
            AdminPartnerServiceReviewRequest requestBody) {
        User reviewer = requireAdmin(request);
        ProviderServiceChangeRequest changeRequest = requireRequest(requestId);
        ensurePending(changeRequest);
        String message = requireText(requestBody != null ? requestBody.message() : null,
                "Vui lòng nhập lý do từ chối yêu cầu dịch vụ.");
        changeRequest.setStatus(STATUS_REJECTED);
        changeRequest.setReviewer(reviewer);
        changeRequest.setReviewedAt(LocalDateTime.now());
        changeRequest.setAdminMessage(message);
        return mapRequest(changeRequestRepository.save(changeRequest));
    }

    private void applyDraftFields(ProviderProfile provider, ProviderServiceChangeRequest changeRequest,
            PartnerServiceChangeRequestUpsertRequest requestBody) {
        if (requestBody == null) {
            throw new BadRequestException("Dữ liệu dịch vụ không hợp lệ.");
        }
        ProviderService providerService = null;
        if (requestBody.providerServiceId() != null) {
            providerService = requireOwnedService(provider.getId(), requestBody.providerServiceId());
            ensureNoPendingUpdate(provider.getId(), providerService.getId(), changeRequest.getId());
            changeRequest.setProviderService(providerService);
            changeRequest.setRequestType(TYPE_UPDATE);
        } else {
            changeRequest.setProviderService(null);
            changeRequest.setRequestType(TYPE_CREATE);
        }
        changeRequest.setCategoryIds(null);
        changeRequest.setServiceName(mapper.normalizeBlank(requestBody.serviceName()));
        changeRequest.setPhotoUrls(toTextLines(normalizePhotoUrls(requestBody.photoUrls(), false)));
        changeRequest.setPriceAmount(requestBody.priceAmount());
        changeRequest.setCurrencyCode(normalizeCurrency(requestBody.currencyCode()));
        changeRequest.setPriceUnit(mapper.firstNonBlank(requestBody.priceUnit(), "SESSION"));
        changeRequest.setDescription(mapper.normalizeBlank(requestBody.description()));
    }

    private void submit(ProviderServiceChangeRequest changeRequest) {
        requireText(changeRequest.getServiceName(), "Tên dịch vụ là bắt buộc.");
        List<String> photos = mapper.parseTextLines(changeRequest.getPhotoUrls());
        if (photos.size() < MIN_PHOTOS || photos.size() > MAX_PHOTOS) {
            throw new BadRequestException("Vui lòng cung cấp từ 1 đến 5 ảnh mô tả dịch vụ.");
        }
        if (changeRequest.getPriceAmount() == null || changeRequest.getPriceAmount().signum() < 0) {
            throw new BadRequestException("Giá dịch vụ phải lớn hơn hoặc bằng 0.");
        }
        requireText(changeRequest.getPriceUnit(), "Đơn vị tính là bắt buộc.");
        requireText(changeRequest.getDescription(), "Mô tả dịch vụ là bắt buộc.");
        if (TYPE_UPDATE.equalsIgnoreCase(changeRequest.getRequestType())
                && changeRequest.getProviderService() == null) {
            throw new BadRequestException("Yêu cầu cập nhật cần liên kết dịch vụ hiện có.");
        }
        changeRequest.setCategoryIds(null);
        changeRequest.setStatus(STATUS_PENDING);
        changeRequest.setSubmittedAt(LocalDateTime.now());
        changeRequest.setReviewedAt(null);
        changeRequest.setReviewer(null);
        changeRequest.setAdminMessage(null);
    }

    private ProviderService applyApprovedCreate(ProviderServiceChangeRequest changeRequest) {
        ProviderService providerService = new ProviderService();
        providerService.setProvider(changeRequest.getProvider());
        providerService.setService(resolveCatalogServiceForRequest(changeRequest));
        applyApprovedFields(providerService, changeRequest);
        return providerServiceRepository.save(providerService);
    }

    private ProviderService applyApprovedUpdate(ProviderServiceChangeRequest changeRequest) {
        ProviderService providerService = requireOwnedService(changeRequest.getProvider().getId(),
                changeRequest.getProviderService().getId());
        providerService.setService(resolveCatalogServiceForRequest(changeRequest));
        applyApprovedFields(providerService, changeRequest);
        return providerServiceRepository.save(providerService);
    }

    private void applyApprovedFields(ProviderService providerService, ProviderServiceChangeRequest changeRequest) {
        providerService.setCustomName(changeRequest.getServiceName());
        providerService.setShortDescription(buildShortDescription(changeRequest.getDescription()));
        providerService.setDescription(changeRequest.getDescription());
        providerService.setDurationMinutes(defaultDuration(providerService.getService()));
        providerService.setDurationType(defaultDurationType(providerService.getDurationType()));
        providerService.setPriceAmount(changeRequest.getPriceAmount());
        providerService.setCurrencyCode(mapper.firstNonBlank(changeRequest.getCurrencyCode(),
                providerService.getService() != null ? providerService.getService().getCurrencyCode() : null, "VND"));
        providerService.setPriceUnit(mapper.firstNonBlank(changeRequest.getPriceUnit(),
                providerService.getService() != null ? providerService.getService().getPriceUnit() : null, "SESSION"));
        providerService.setFeatured(Boolean.FALSE);
        providerService.setActive(Boolean.TRUE);
        providerService.setCapacityPerSlot(1);
        providerService.setBookingBufferMinutes(0);
        providerService.setBufferAfterMinutes(0);
        providerService.setDisplayOrder(0);
        providerService.setCategoryIds(changeRequest.getCategoryIds());
        providerService.setPhotoUrls(changeRequest.getPhotoUrls());
        providerService.setApprovalStatus(STATUS_APPROVED);
    }

    private CatalogService resolveCatalogServiceForRequest(ProviderServiceChangeRequest changeRequest) {
        List<Long> categoryIds = mapper.parseLongCsv(changeRequest.getCategoryIds());
        if (categoryIds.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn loại dịch vụ từ bảng service_categories trước khi duyệt.");
        }
        Long leafCategoryId = categoryIds.get(categoryIds.size() - 1);
        List<CatalogService> existing = catalogServiceRepository
                .findActiveByNameAndCategory(changeRequest.getServiceName(), leafCategoryId);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        ServiceCategory category = serviceCategoryRepository.findById(leafCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục dịch vụ."));
        CatalogService catalogService = new CatalogService();
        catalogService.setServiceCode(generateServiceCode());
        catalogService.setCategory(category);
        catalogService.setName(changeRequest.getServiceName());
        catalogService.setSlug(generateServiceSlug(changeRequest.getServiceName()));
        catalogService.setShortDescription(buildShortDescription(changeRequest.getDescription()));
        catalogService.setDescription(changeRequest.getDescription());
        catalogService.setDefaultDurationMinutes(60);
        catalogService.setBasePriceAmount(changeRequest.getPriceAmount());
        catalogService.setCurrencyCode(mapper.firstNonBlank(changeRequest.getCurrencyCode(), "VND"));
        catalogService.setPriceUnit(mapper.firstNonBlank(changeRequest.getPriceUnit(), "SESSION"));
        catalogService.setRequiresConsultation(false);
        catalogService.setActive(true);
        return catalogServiceRepository.save(catalogService);
    }

    private PartnerServiceChangeRequestResponse mapRequest(ProviderServiceChangeRequest request) {
        List<Long> categoryIds = mapper.parseLongCsv(request.getCategoryIds());
        List<String> photoUrls = mapper.parseTextLines(request.getPhotoUrls());
        return PartnerServiceChangeRequestResponse.builder()
                .id(request.getId())
                .providerId(request.getProvider() != null ? request.getProvider().getId() : null)
                .providerName(request.getProvider() != null ? request.getProvider().getBusinessName() : null)
                .providerServiceId(request.getProviderService() != null ? request.getProviderService().getId() : null)
                .requestType(request.getRequestType())
                .status(request.getStatus())
                .statusLabel(statusLabel(request.getStatus()))
                .categoryIds(categoryIds)
                .categories(resolveCategories(categoryIds).stream().map(this::mapCategory).toList())
                .serviceName(request.getServiceName())
                .photoUrls(photoUrls)
                .priceAmount(defaultMoney(request.getPriceAmount()))
                .priceDisplay(mapper.formatMoney(request.getPriceAmount()))
                .currencyCode(mapper.firstNonBlank(request.getCurrencyCode(), "VND"))
                .priceUnit(request.getPriceUnit())
                .priceUnitLabel(priceUnitLabel(request.getPriceUnit()))
                .description(request.getDescription())
                .adminMessage(request.getAdminMessage())
                .createdAt(formatDateTime(request.getCreatedAt()))
                .updatedAt(formatDateTime(request.getUpdatedAt()))
                .submittedAt(formatDateTime(request.getSubmittedAt()))
                .reviewedAt(formatDateTime(request.getReviewedAt()))
                .reviewerId(request.getReviewer() != null ? request.getReviewer().getId() : null)
                .reviewerName(request.getReviewer() != null ? request.getReviewer().getFullName() : null)
                .changes(buildChanges(request, categoryIds, photoUrls))
                .build();
    }

    private List<PartnerServiceChangeItemResponse> buildChanges(ProviderServiceChangeRequest request,
            List<Long> categoryIds, List<String> photoUrls) {
        ProviderService current = request.getProviderService();
        List<PartnerServiceChangeItemResponse> changes = new ArrayList<>();
        String proposedCategoryLabel = categoryIds == null || categoryIds.isEmpty()
                ? "Admin sẽ phân loại khi duyệt"
                : categoryLabel(categoryIds);
        changes.add(change("categories", "Loại dịch vụ (admin phân loại)", currentCategoryLabel(current),
                proposedCategoryLabel));
        changes.add(change("serviceName", "Tên dịch vụ", currentName(current), request.getServiceName()));
        changes.add(change("photoUrls", "Ảnh mô tả", currentPhotoLabel(current), photoLabel(photoUrls)));
        changes.add(change("price", "Giá và đơn vị", currentPriceLabel(current),
                mapper.formatMoney(request.getPriceAmount()) + " / " + priceUnitLabel(request.getPriceUnit())));
        changes.add(change("description", "Mô tả", current != null ? current.getDescription() : null,
                request.getDescription()));
        return changes;
    }

    private PartnerServiceChangeItemResponse change(String field, String label, String currentValue,
            String proposedValue) {
        String current = mapper.firstNonBlank(currentValue, "—");
        String proposed = mapper.firstNonBlank(proposedValue, "—");
        return PartnerServiceChangeItemResponse.builder()
                .field(field)
                .label(label)
                .currentValue(current)
                .proposedValue(proposed)
                .changed(!Objects.equals(current, proposed))
                .build();
    }

    private List<Long> normalizeReviewCategoryIds(AdminPartnerServiceReviewRequest requestBody) {
        List<Long> rawCategoryIds = requestBody != null ? requestBody.categoryIds() : null;
        List<Long> requested = rawCategoryIds == null ? List.of()
                : rawCategoryIds.stream().filter(Objects::nonNull).distinct().toList();
        if (requested.isEmpty() && requestBody != null && requestBody.categoryId() != null) {
            requested = List.of(requestBody.categoryId());
        }
        if (requested.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn loại dịch vụ từ bảng service_categories trước khi duyệt.");
        }
        long existingCount = serviceCategoryRepository.findAllById(requested).stream()
                .filter(category -> Boolean.TRUE.equals(category.getActive()))
                .count();
        if (existingCount != requested.size()) {
            throw new BadRequestException("Danh mục dịch vụ không hợp lệ hoặc đã bị ẩn.");
        }
        return requested;
    }

    private List<String> normalizePhotoUrls(List<String> rawPhotoUrls, boolean required) {
        List<String> photos = rawPhotoUrls == null ? List.of()
                : rawPhotoUrls.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(value -> !value.isBlank())
                        .distinct()
                        .toList();
        if (photos.size() > MAX_PHOTOS) {
            throw new BadRequestException("Chỉ được lưu tối đa 5 ảnh mô tả dịch vụ.");
        }
        if (required && photos.size() < MIN_PHOTOS) {
            throw new BadRequestException("Vui lòng cung cấp ít nhất 1 ảnh mô tả dịch vụ.");
        }
        return photos;
    }

    private List<ServiceCategory> resolveCategories(List<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : serviceCategoryRepository.findAllById(ids);
    }

    private ProviderServiceChangeRequest requireOwnedRequest(Long providerId, Long requestId) {
        return changeRequestRepository.findDetailedByProviderIdAndId(providerId, requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản nháp/yêu cầu dịch vụ."));
    }

    private ProviderServiceChangeRequest requireRequest(Long requestId) {
        return changeRequestRepository.findDetailedById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu dịch vụ."));
    }

    private ProviderService requireOwnedService(Long providerId, Long providerServiceId) {
        return providerServiceRepository.findDetailByProviderIdAndId(providerId, providerServiceId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy dịch vụ thuộc nhà cung cấp hiện tại."));
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(
                        userRole -> userRole.getRole() != null && RoleType.ADMIN.equals(userRole.getRole().getCode()));
        if (!isAdmin) {
            throw new UnauthorizedException("Bạn không có quyền admin.");
        }
        return user;
    }

    private void ensureDraftLimit(Long providerId, Long currentRequestId) {
        long draftCount = changeRequestRepository.countByProvider_IdAndStatus(providerId, STATUS_DRAFT);
        if (currentRequestId == null && draftCount >= MAX_DRAFTS_PER_PROVIDER) {
            throw new BadRequestException("Partner chỉ được lưu tối đa 3 bản nháp dịch vụ.");
        }
    }

    private void ensureNoPendingUpdate(Long providerId, Long providerServiceId, Long currentRequestId) {
        boolean hasPending = currentRequestId == null
                ? changeRequestRepository.existsByProvider_IdAndProviderService_IdAndStatus(providerId,
                        providerServiceId, STATUS_PENDING)
                : changeRequestRepository.existsByProvider_IdAndProviderService_IdAndStatusAndIdNot(providerId,
                        providerServiceId, STATUS_PENDING, currentRequestId);
        if (hasPending) {
            throw new BadRequestException("Dịch vụ này đang có yêu cầu cập nhật chờ admin duyệt.");
        }
    }

    private void ensureDraft(ProviderServiceChangeRequest request) {
        if (!STATUS_DRAFT.equalsIgnoreCase(mapper.firstNonBlank(request.getStatus(), ""))) {
            throw new BadRequestException("Chỉ bản nháp mới được chỉnh sửa/xóa/gửi duyệt.");
        }
    }

    private void ensurePending(ProviderServiceChangeRequest request) {
        if (!STATUS_PENDING.equalsIgnoreCase(mapper.firstNonBlank(request.getStatus(), ""))) {
            throw new BadRequestException("Chỉ yêu cầu đang chờ duyệt mới có thể xử lý.");
        }
    }

    private String normalizeStatusFilter(String status) {
        String normalized = mapper.firstNonBlank(status, "PENDING_REVIEW").toUpperCase(Locale.ROOT);
        return "ALL".equals(normalized) ? null : normalized;
    }

    private String normalizeCurrency(String currency) {
        return mapper.firstNonBlank(currency, "VND").toUpperCase(Locale.ROOT);
    }

    private String requireText(String value, String message) {
        String normalized = mapper.normalizeBlank(value);
        if (normalized == null) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String toCsv(List<Long> values) {
        return values == null || values.isEmpty() ? null
                : values.stream().filter(Objects::nonNull).distinct().map(String::valueOf)
                        .reduce((left, right) -> left + "," + right).orElse(null);
    }

    private String toTextLines(List<String> values) {
        return values == null || values.isEmpty() ? null
                : values.stream().filter(Objects::nonNull).map(String::trim).filter(value -> !value.isBlank())
                        .distinct().reduce((left, right) -> left + "\n" + right).orElse(null);
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int defaultDuration(CatalogService service) {
        return service != null && service.getDefaultDurationMinutes() != null && service.getDefaultDurationMinutes() > 0
                ? service.getDefaultDurationMinutes()
                : 60;
    }

    private String defaultDurationType(String currentDurationType) {
        return mapper.firstNonBlank(currentDurationType, DEFAULT_DURATION_TYPE).toUpperCase(Locale.ROOT);
    }

    private String buildShortDescription(String description) {
        String normalized = mapper.normalizeBlank(description);
        if (normalized == null) {
            return null;
        }
        return normalized.length() <= 255 ? normalized : normalized.substring(0, 252) + "...";
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(PartnerMappingSupport.DATE_TIME_VIEW);
    }

    private String statusLabel(String status) {
        return switch (mapper.firstNonBlank(status, "").toUpperCase(Locale.ROOT)) {
            case STATUS_DRAFT -> "Bản nháp";
            case STATUS_PENDING -> "Chờ admin duyệt";
            case STATUS_APPROVED -> "Đã duyệt";
            case STATUS_REJECTED -> "Đã từ chối";
            default -> mapper.firstNonBlank(status, "Không rõ");
        };
    }

    private String priceUnitLabel(String priceUnit) {
        String normalized = mapper.firstNonBlank(priceUnit, "SESSION").toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "HOUR", "PER_HOUR" -> "giờ";
            case "DAY", "PER_DAY" -> "ngày";
            case "PET", "PER_PET" -> "thú cưng";
            case "VISIT", "ONCE", "SESSION", "PER_SESSION" -> "1 lần";
            default -> priceUnit;
        };
    }

    private String categoryLabel(List<Long> categoryIds) {
        List<ServiceCategory> categories = resolveCategories(categoryIds);
        return categories.isEmpty() ? "—" : categories.stream().map(ServiceCategory::getName).toList().toString();
    }

    private String currentCategoryLabel(ProviderService service) {
        if (service == null) {
            return null;
        }
        List<Long> ids = mapper.parseLongCsv(service.getCategoryIds());
        if (ids.isEmpty() && service.getService() != null && service.getService().getCategory() != null) {
            ids = List.of(service.getService().getCategory().getId());
        }
        return categoryLabel(ids);
    }

    private String currentName(ProviderService service) {
        return service == null ? null
                : mapper.firstNonBlank(service.getCustomName(),
                        service.getService() != null ? service.getService().getName() : null);
    }

    private String currentPhotoLabel(ProviderService service) {
        return service == null ? null : photoLabel(mapper.parseTextLines(service.getPhotoUrls()));
    }

    private String photoLabel(List<String> photoUrls) {
        return photoUrls == null || photoUrls.isEmpty() ? "—" : photoUrls.size() + " ảnh";
    }

    private String currentPriceLabel(ProviderService service) {
        if (service == null) {
            return null;
        }
        return mapper.formatMoney(service.getPriceAmount()) + " / " + priceUnitLabel(service.getPriceUnit());
    }

    private PartnerServiceCategoryResponse mapCategory(ServiceCategory category) {
        return PartnerServiceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }

    private String generateServiceCode() {
        String code;
        do {
            code = "CSV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
        } while (catalogServiceRepository.existsByServiceCode(code));
        return code;
    }

    private String generateServiceSlug(String serviceName) {
        String base = toSlug(serviceName);
        if (base.isBlank()) {
            base = "partner-service";
        }
        String slug = base;
        int index = 1;
        while (catalogServiceRepository.existsBySlug(slug)) {
            slug = base + "-" + index++;
        }
        return slug;
    }

    private String toSlug(String value) {
        String normalized = Normalizer.normalize(mapper.firstNonBlank(value, "partner-service"), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized;
    }
}