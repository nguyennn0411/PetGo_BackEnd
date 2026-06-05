package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerServiceRequest;
import com.example.petgo.dto.partner.PartnerServiceResponse;
import com.example.petgo.entity.CatalogService;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.CatalogServiceRepository;
import com.example.petgo.repository.ProviderServiceRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerServiceManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerServiceManagementServiceImpl implements PartnerServiceManagementService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final ProviderServiceRepository providerServiceRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PartnerServiceResponse> listServices(HttpServletRequest request) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return providerServiceRepository.findAllDetailsByProviderId(provider.getId()).stream()
                .map(mapper::mapService)
                .toList();
    }

    @Override
    @Transactional
    public PartnerServiceResponse createService(HttpServletRequest request, PartnerServiceRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        CatalogService catalogService = requireCatalogService(requestBody.serviceId());

        ProviderService providerService = new ProviderService();
        providerService.setProvider(provider);
        providerService.setService(catalogService);
        applyMutableFields(providerService, catalogService, requestBody);
        ProviderService saved = providerServiceRepository.save(providerService);
        return mapper.mapService(saved);
    }

    @Override
    @Transactional
    public PartnerServiceResponse updateService(HttpServletRequest request, Long providerServiceId,
            PartnerServiceRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        ProviderService providerService = requireOwnedService(provider.getId(), providerServiceId);
        CatalogService catalogService = requireCatalogService(requestBody.serviceId());
        providerService.setService(catalogService);
        applyMutableFields(providerService, catalogService, requestBody);
        return mapper.mapService(providerServiceRepository.save(providerService));
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

    private void applyMutableFields(ProviderService providerService, CatalogService catalogService,
            PartnerServiceRequest requestBody) {
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
        providerService.setDisplayOrder(Optional.ofNullable(requestBody.displayOrder()).orElse(0));
    }
}