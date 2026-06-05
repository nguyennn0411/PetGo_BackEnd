package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shop."));

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
        if (p.getPrimaryAddressLine1() != null) parts.add(p.getPrimaryAddressLine1());
        if (p.getWard() != null) parts.add(p.getWard());
        if (p.getDistrict() != null) parts.add(p.getDistrict());
        if (p.getCity() != null) parts.add(p.getCity());
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shop yêu cầu."));
        
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shop yêu cầu."));
        
        provider.setStatus(request.status());
        providerProfileRepository.save(provider);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> getAllCategories() {
        return serviceCategoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .toList();
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục."));
        mapCategoryRequestToEntity(request, category);
        category = serviceCategoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục."));
        // Thay vì xóa cứng, ta có thể set active = false
        category.setActive(false);
        serviceCategoryRepository.save(category);
    }

    private void mapCategoryRequestToEntity(ServiceCategoryRequest request, ServiceCategory category) {
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setIconKey(request.getIconKey());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setActive(request.getActive() != null ? request.getActive() : true);
    }

    private ServiceCategoryResponse mapToCategoryResponse(ServiceCategory category) {
        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .iconKey(category.getIconKey())
                .sortOrder(category.getSortOrder())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
