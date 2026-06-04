package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final HomeSliderRepository homeSliderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponse> getPendingProviders() {
        return providerProfileRepository.findByVerificationStatus("PENDING").stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponse> getVerifiedProviders() {
        return providerProfileRepository.findByVerificationStatus("VERIFIED").stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderDetailResponse getProviderDetail(Long providerId) {
        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp."));

        List<ProviderService> services = providerServiceRepository.findByProvider_Id(providerId);

        return ProviderDetailResponse.builder()
                .id(provider.getId())
                .slug(provider.getSlug())
                .name(provider.getBusinessName())
                .headline(provider.getHeadline())
                .description(provider.getDescription())
                .providerType(provider.getProviderType())
                .verificationStatus(provider.getVerificationStatus())
                .featured(provider.getFeatured())
                .hot(provider.getHot())
                .instantBooking(provider.getAcceptsInstantBooking())
                .acceptsMembership(provider.getAcceptsMembership())
                .yearsExperience(provider.getYearsExperience())
                .rating(provider.getAverageRating())
                .reviewsCount(provider.getTotalReviews())
                .address(buildAddress(provider))
                .city(provider.getCity())
                .province(provider.getProvince())
                .emergencyPhone(provider.getEmergencyPhone())
                .mainImage(provider.getMainImageUrl())
                .bannerImage(provider.getCoverImageUrl())
                .services(services.stream().map(this::mapToServiceDetail).toList())
                .build();
    }

    private ProviderDetailServiceItemResponse mapToServiceDetail(ProviderService s) {
        return ProviderDetailServiceItemResponse.builder()
                .id(s.getId())
                .name(s.getService() != null ? s.getService().getName() : s.getCustomName())
                .price(s.getPriceAmount())
                .priceDisplay(s.getPriceAmount() != null ? s.getPriceAmount().toString() : "0")
                .currencyCode(s.getCurrencyCode())
                .build();
    }

    private String buildAddress(ProviderProfile p) {
        List<String> parts = new ArrayList<>();
        if (p.getPrimaryAddressLine1() != null)
            parts.add(p.getPrimaryAddressLine1());
        if (p.getWard() != null)
            parts.add(p.getWard());
        if (p.getDistrict() != null)
            parts.add(p.getDistrict());
        if (p.getCity() != null)
            parts.add(p.getCity());
        return String.join(", ", parts);
    }

    private ProviderResponse mapToResponse(ProviderProfile p) {
        return ProviderResponse.builder()
                .id(p.getId())
                .providerCode(p.getProviderCode())
                .businessName(p.getBusinessName())
                .ownerName(p.getUser() != null ? p.getUser().getFullName() : null)
                .email(p.getUser() != null ? p.getUser().getEmail() : null)
                .phoneNumber(p.getUser() != null ? p.getUser().getPhoneNumber() : p.getEmergencyPhone())
                .address(p.getPrimaryAddressLine1())
                .verificationStatus(p.getVerificationStatus())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void updateProviderStatus(ProviderVerificationRequest request) {
        ProviderProfile provider = providerProfileRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu của nhà cung cấp."));

        provider.setVerificationStatus(request.status());
        if ("VERIFIED".equalsIgnoreCase(request.status())) {
            provider.setStatus("ACTIVE");
        }
        providerProfileRepository.save(provider);
    }

    @Override
    @Transactional
    public void updateProviderAccountStatus(ProviderVerificationRequest request) {
        ProviderProfile provider = providerProfileRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu của nhà cung cấp."));

        provider.setStatus(request.status());
        providerProfileRepository.save(provider);
    }

    @Override
    @Transactional
    public List<ServiceCategoryResponse> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryRepository.findAllByOrderByNameAscIdAsc();
        syncActiveAncestors(categories);
        return buildCategoryTree(categories, null);
    }

    @Override
    @Transactional
    public ServiceCategoryResponse createCategory(ServiceCategoryRequest request) {
        ServiceCategory category = new ServiceCategory();
        mapCategoryRequestToEntity(request, category);
        category = serviceCategoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public ServiceCategoryResponse updateCategory(Long id, ServiceCategoryRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục."));
        mapCategoryRequestToEntity(request, category);
        category = serviceCategoryRepository.saveAndFlush(category);
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục."));
        deactivateCategoryTree(category);
        serviceCategoryRepository.saveAndFlush(category);
    }

    private void mapCategoryRequestToEntity(ServiceCategoryRequest request, ServiceCategory category) {
        String normalizedName = normalizeRequired(request.getName(), "Tên danh mục không được để trống");
        category.setName(normalizedName);
        category.setDescription(normalizeNullable(request.getDescription()));
        category.setParent(resolveParent(request.getParentId(), category.getId()));
        Boolean active = request.getActive() != null ? request.getActive() : true;
        category.setActive(active);
        if (Boolean.TRUE.equals(active)) {
            activateCategoryAncestors(category);
        } else {
            deactivateCategoryTree(category);
        }
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private ServiceCategoryResponse mapToCategoryResponse(ServiceCategory category) {
        return mapToCategoryResponse(category, List.of());
    }

    private ServiceCategoryResponse mapToCategoryResponse(ServiceCategory category,
            List<ServiceCategoryResponse> children) {
        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .children(children)
                .build();
    }

    private List<ServiceCategoryResponse> buildCategoryTree(List<ServiceCategory> categories, Long parentId) {
        return categories.stream()
                .filter(category -> Objects.equals(parentIdOf(category), parentId))
                .map(category -> mapToCategoryResponse(category, buildCategoryTree(categories, category.getId())))
                .toList();
    }

    private Long parentIdOf(ServiceCategory category) {
        return category.getParent() != null ? category.getParent().getId() : null;
    }

    private ServiceCategory resolveParent(Long parentId, Long currentCategoryId) {
        if (parentId == null) {
            return null;
        }
        if (Objects.equals(parentId, currentCategoryId)) {
            throw new BadRequestException("Danh mục cha không được trùng với danh mục hiện tại.");
        }
        ServiceCategory parent = serviceCategoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha."));
        if (currentCategoryId != null && createsCycle(parent, currentCategoryId)) {
            throw new BadRequestException("Danh mục cha không hợp lệ vì tạo vòng lặp phân cấp.");
        }
        return parent;
    }

    private boolean createsCycle(ServiceCategory parent, Long currentCategoryId) {
        ServiceCategory cursor = parent;
        while (cursor != null) {
            if (Objects.equals(cursor.getId(), currentCategoryId)) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private void deactivateCategoryTree(ServiceCategory category) {
        category.setActive(false);
        category.getChildren().forEach(this::deactivateCategoryTree);
    }

    private void activateCategoryAncestors(ServiceCategory category) {
        ServiceCategory parent = category.getParent();
        Set<Long> visitedIds = new HashSet<>();
        while (parent != null && visitedIds.add(parent.getId())) {
            parent.setActive(true);
            parent = parent.getParent();
        }
    }

    private void syncActiveAncestors(List<ServiceCategory> categories) {
        boolean hasChanged = false;
        for (ServiceCategory category : categories) {
            if (!Boolean.TRUE.equals(category.getActive())) {
                continue;
            }

            ServiceCategory parent = category.getParent();
            Set<Long> visitedIds = new HashSet<>();
            while (parent != null && visitedIds.add(parent.getId())) {
                if (!Boolean.TRUE.equals(parent.getActive())) {
                    parent.setActive(true);
                    hasChanged = true;
                }
                parent = parent.getParent();
            }
        }

        if (hasChanged) {
            serviceCategoryRepository.saveAllAndFlush(categories);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeSliderResponse> getAllHomeSliders() {
        return homeSliderRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(this::mapToHomeSliderResponse)
                .toList();
    }

    @Override
    @Transactional
    public HomeSliderResponse createHomeSlider(HomeSliderRequest request) {
        HomeSlider slider = new HomeSlider();
        mapHomeSliderRequestToEntity(request, slider);
        return mapToHomeSliderResponse(homeSliderRepository.save(slider));
    }

    @Override
    @Transactional
    public HomeSliderResponse updateHomeSlider(Long id, HomeSliderRequest request) {
        HomeSlider slider = homeSliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slider trang chủ."));
        mapHomeSliderRequestToEntity(request, slider);
        return mapToHomeSliderResponse(homeSliderRepository.saveAndFlush(slider));
    }

    @Override
    @Transactional
    public void deleteHomeSlider(Long id) {
        if (!homeSliderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy slider trang chủ.");
        }
        homeSliderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HomeSliderResponse updateHomeSliderVisibility(Long id, Boolean active) {
        if (active == null) {
            throw new BadRequestException("Trạng thái hiển thị không được để trống.");
        }
        HomeSlider slider = homeSliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slider trang chủ."));
        slider.setActive(active);
        return mapToHomeSliderResponse(homeSliderRepository.saveAndFlush(slider));
    }

    private void mapHomeSliderRequestToEntity(HomeSliderRequest request, HomeSlider slider) {
        slider.setTitle(normalizeRequired(request.title(), "Tiêu đề slider không được để trống"));
        slider.setSubtitle(normalizeNullable(request.subtitle()));
        slider.setImageUrl(normalizeRequired(request.imageUrl(), "Ảnh slider không được để trống"));
        slider.setCtaLabel(normalizeNullable(request.ctaLabel()));
        slider.setCtaUrl(normalizeNullable(request.ctaUrl()));
        slider.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        slider.setActive(request.active() != null ? request.active() : true);
    }

    private HomeSliderResponse mapToHomeSliderResponse(HomeSlider slider) {
        return HomeSliderResponse.builder()
                .id(slider.getId())
                .title(slider.getTitle())
                .subtitle(slider.getSubtitle())
                .imageUrl(slider.getImageUrl())
                .ctaLabel(slider.getCtaLabel())
                .ctaUrl(slider.getCtaUrl())
                .sortOrder(slider.getSortOrder())
                .active(slider.getActive())
                .build();
    }
}
