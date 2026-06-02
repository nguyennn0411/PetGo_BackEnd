package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerCatalogServiceOptionResponse;
import com.example.petgo.dto.partner.PartnerServiceRequest;
import com.example.petgo.dto.partner.PartnerServiceResponse;
import com.example.petgo.entity.CatalogService;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.entity.RegistrationApplication;
import com.example.petgo.entity.RegistrationType;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.CatalogServiceRepository;
import com.example.petgo.repository.ProviderServiceRepository;
import com.example.petgo.repository.RegistrationApplicationRepository;
import com.example.petgo.service.impl.RegistrationMapperSupport;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerServiceManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PartnerServiceManagementServiceImpl implements PartnerServiceManagementService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final ProviderServiceRepository providerServiceRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final RegistrationApplicationRepository registrationApplicationRepository;
    private final RegistrationMapperSupport registrationMapperSupport;

    @Override
    @Transactional(readOnly = true)
    public List<PartnerServiceResponse> listServices(HttpServletRequest request) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return providerServiceRepository.findAllDetailsByProviderId(provider.getId()).stream()
                .map(mapper::mapService)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerCatalogServiceOptionResponse> listCatalogOptions(HttpServletRequest request) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Set<Long> registeredCategoryIds = resolveRegisteredCategoryIds(provider);
        return catalogServiceRepository.findActiveDetails().stream()
                .filter(service -> registeredCategoryIds.isEmpty()
                        || isCategoryAllowed(service.getCategory(), registeredCategoryIds))
                .map(this::mapCatalogOption)
                .toList();
    }

    @Override
    @Transactional
    public PartnerServiceResponse createService(HttpServletRequest request, PartnerServiceRequest requestBody) {
        throw new BadRequestException("Vui lòng dùng luồng Yêu cầu tạo dịch vụ để admin duyệt.");
    }

    @Override
    @Transactional
    public PartnerServiceResponse updateService(HttpServletRequest request, Long providerServiceId,
            PartnerServiceRequest requestBody) {
        throw new BadRequestException("Vui lòng dùng luồng Yêu cầu cập nhật dịch vụ để admin duyệt.");
    }

    @Override
    @Transactional
    public PartnerServiceResponse updateServiceStatus(HttpServletRequest request, Long providerServiceId,
            Boolean active) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderService providerService = requireOwnedService(provider.getId(), providerServiceId);
        providerService.setActive(Boolean.TRUE.equals(active));
        return mapper.mapService(providerServiceRepository.save(providerService));
    }

    @Override
    @Transactional
    public PartnerServiceResponse archiveService(HttpServletRequest request, Long providerServiceId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderService providerService = requireOwnedService(provider.getId(), providerServiceId);
        providerService.setActive(false);
        return mapper.mapService(providerServiceRepository.save(providerService));
    }

    private CatalogService requireCatalogService(Long serviceId) {
        return catalogServiceRepository.findActiveDetailById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ catalog đang hoạt động."));
    }

    private ProviderService requireOwnedService(Long providerId, Long providerServiceId) {
        return providerServiceRepository.findDetailByProviderIdAndId(providerId, providerServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ thuộc shop hiện tại."));
    }

    private void validateCatalogServiceAllowed(ProviderProfile provider, CatalogService catalogService) {
        Set<Long> registeredCategoryIds = resolveRegisteredCategoryIds(provider);
        if (!registeredCategoryIds.isEmpty()
                && !isCategoryAllowed(catalogService.getCategory(), registeredCategoryIds)) {
            throw new BadRequestException("Dịch vụ không thuộc nhóm dịch vụ shop đã đăng ký.");
        }
    }

    private void ensureNoDuplicateCatalogService(Long providerId, Long catalogServiceId,
            Long currentProviderServiceId) {
        boolean duplicated = currentProviderServiceId == null
                ? providerServiceRepository.existsByProvider_IdAndService_Id(providerId, catalogServiceId)
                : providerServiceRepository.existsByProvider_IdAndService_IdAndIdNot(providerId, catalogServiceId,
                        currentProviderServiceId);
        if (duplicated) {
            throw new BadRequestException("Shop đã có dịch vụ này. Vui lòng sửa dịch vụ hiện có thay vì tạo trùng.");
        }
    }

    private Set<Long> resolveRegisteredCategoryIds(ProviderProfile provider) {
        if (provider == null || provider.getUser() == null || provider.getUser().getId() == null) {
            return Set.of();
        }
        RegistrationApplication registration = registrationApplicationRepository
                .findByUser_IdAndType(provider.getUser().getId(), RegistrationType.PARTNER)
                .orElse(null);
        if (registration == null) {
            return Set.of();
        }
        return new HashSet<>(registrationMapperSupport.parseLongCsv(registration.getServiceCategoryIds()));
    }

    private boolean isCategoryAllowed(ServiceCategory category, Set<Long> allowedCategoryIds) {
        ServiceCategory cursor = category;
        while (cursor != null) {
            if (allowedCategoryIds.contains(cursor.getId())) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private PartnerCatalogServiceOptionResponse mapCatalogOption(CatalogService service) {
        ServiceCategory category = service.getCategory();
        ServiceCategory parentCategory = category != null ? category.getParent() : null;
        return PartnerCatalogServiceOptionResponse.builder()
                .serviceId(service.getId())
                .serviceName(service.getName())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .parentCategoryId(parentCategory != null ? parentCategory.getId() : null)
                .parentCategoryName(parentCategory != null ? parentCategory.getName() : null)
                .defaultDurationMinutes(service.getDefaultDurationMinutes())
                .basePriceAmount(mapper.defaultMoney(service.getBasePriceAmount()))
                .priceDisplay(mapper.formatMoney(service.getBasePriceAmount()))
                .currencyCode(mapper.firstNonBlank(service.getCurrencyCode(), "VND"))
                .priceUnit(mapper.firstNonBlank(service.getPriceUnit(), "SESSION"))
                .shortDescription(service.getShortDescription())
                .build();
    }

    private void applyMutableFields(ProviderService providerService, CatalogService catalogService,
            PartnerServiceRequest requestBody) {
        if (requestBody.durationMinutes() == null || requestBody.durationMinutes() <= 0) {
            throw new BadRequestException("Thời lượng dịch vụ phải lớn hơn 0.");
        }
        if (requestBody.priceAmount() == null) {
            throw new BadRequestException("Giá dịch vụ là bắt buộc.");
        }
        if (requestBody.priceAmount().signum() < 0) {
            throw new BadRequestException("Giá dịch vụ phải >= 0.");
        }
        providerService.setCustomName(mapper.normalizeBlank(requestBody.customName()));
        providerService.setShortDescription(mapper.normalizeBlank(requestBody.shortDescription()));
        providerService.setDescription(mapper.normalizeBlank(requestBody.description()));
        providerService.setDurationMinutes(requestBody.durationMinutes());
        providerService.setPriceAmount(requestBody.priceAmount());
        providerService.setCurrencyCode(
                mapper.firstNonBlank(requestBody.currencyCode(), catalogService.getCurrencyCode(), "VND"));
        providerService
                .setPriceUnit(mapper.firstNonBlank(requestBody.priceUnit(), catalogService.getPriceUnit(), "SESSION"));
        providerService.setFeatured(Boolean.TRUE.equals(requestBody.featured()));
        providerService.setActive(requestBody.active() == null || Boolean.TRUE.equals(requestBody.active()));
        providerService.setCapacityPerSlot(Optional.ofNullable(requestBody.capacityPerSlot()).orElse(1));
        providerService.setBookingBufferMinutes(Optional.ofNullable(requestBody.bookingBufferMinutes()).orElse(0));
        providerService.setBufferAfterMinutes(Optional.ofNullable(requestBody.bookingBufferMinutes()).orElse(0));
        providerService.setDisplayOrder(Optional.ofNullable(requestBody.displayOrder()).orElse(0));
    }
}