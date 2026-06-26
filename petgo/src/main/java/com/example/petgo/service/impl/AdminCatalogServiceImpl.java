package com.example.petgo.service.impl;

import com.example.petgo.dto.AdminServiceCreateRequest;
import com.example.petgo.dto.AdminServiceUpdateRequest;
import com.example.petgo.dto.CatalogServiceResponse;
import com.example.petgo.dto.PriceTierDTO;
import com.example.petgo.entity.AreaServiceConfig;
import com.example.petgo.entity.CatalogService;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.entity.ServicePriceTier;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.AreaServiceConfigRepository;
import com.example.petgo.repository.CatalogServiceRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import com.example.petgo.repository.ServicePriceTierRepository;
import com.example.petgo.repository.ShippingBookingRepository;
import com.example.petgo.service.AdminCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCatalogServiceImpl implements AdminCatalogService {

    private final CatalogServiceRepository catalogServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final AreaServiceConfigRepository areaServiceConfigRepository;
    private final ShippingBookingRepository shippingBookingRepository;
    private final ServicePriceTierRepository servicePriceTierRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> getAllServices() {
        Set<Long> bookableIds = new HashSet<>(areaServiceConfigRepository.findDistinctServiceIdsByActiveTrue());
        Map<Long, List<Long>> serviceAreaIds = new HashMap<>();
        for (Object[] pair : areaServiceConfigRepository.findActiveServiceAreaPairs()) {
            Long serviceId = (Long) pair[0];
            Long areaId = (Long) pair[1];
            serviceAreaIds.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(areaId);
        }
        return catalogServiceRepository.findActiveDetails()
                .stream().map(s -> toResponse(s, bookableIds, serviceAreaIds)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogServiceResponse getServiceById(Long id) {
        CatalogService s = catalogServiceRepository.findActiveDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));
        List<AreaServiceConfig> configs = areaServiceConfigRepository.findActiveByServiceIdWithArea(id);
        boolean bookable = !configs.isEmpty();
        List<Long> areaIds = configs
                .stream().map(asc -> asc.getArea().getId()).toList();
        return toResponse(s, bookable ? Set.of(id) : Set.of(), Map.of(id, areaIds));
    }

    @Override
    @Transactional
    public CatalogServiceResponse createService(AdminServiceCreateRequest request) {
        List<ServiceCategory> categories = serviceCategoryRepository.findAllById(request.getCategoryIds());
        if (categories.isEmpty()) {
            throw new BadRequestException("Không tìm thấy danh mục nào.");
        }

        String slug = request.getName().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
        String baseSlug = slug;
        int counter = 1;
        while (catalogServiceRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        CatalogService service = new CatalogService();
        service.setServiceCode(request.getServiceCode());
        service.setName(request.getName().trim());
        service.setSlug(slug);
        service.setCategories(categories);
        service.setShortDescription(request.getShortDescription());
        service.setDescription(request.getDescription());
        service.setDefaultDurationMinutes(request.getDefaultDurationMinutes() != null ? request.getDefaultDurationMinutes() : 30);
        service.setBasePriceAmount(request.getBasePriceAmount());
        service.setCurrencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode() : "VND");
        service.setPriceUnit(request.getPriceUnit() != null ? request.getPriceUnit() : "SESSION");
        service.setImageUrl(request.getImageUrl());
        service.setActive(request.getActive() != null ? request.getActive() : true);
        service.setBookingType(request.getBookingType() != null ? request.getBookingType().toUpperCase() : "SHORT");

        CatalogService saved = catalogServiceRepository.save(service);
        savePriceTiers(saved, request.getPriceTiers());
        return toResponse(catalogServiceRepository.findById(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        CatalogService service = catalogServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));

        if (shippingBookingRepository.existsByService_Id(id)) {
            throw new BadRequestException("Không thể xóa dịch vụ đã có lịch hẹn. Vui lòng tắt (active = false) thay vì xóa.");
        }

        areaServiceConfigRepository.deleteByServiceId(id);
        servicePriceTierRepository.deleteByServiceId(id);
        catalogServiceRepository.delete(service);
    }

    @Override
    @Transactional
    public CatalogServiceResponse updateService(Long id, AdminServiceUpdateRequest request) {
        CatalogService service = catalogServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));

        if (request.getName() != null) service.setName(request.getName().trim());
        if (request.getShortDescription() != null) service.setShortDescription(request.getShortDescription());
        if (request.getDescription() != null) service.setDescription(request.getDescription());
        if (request.getDefaultDurationMinutes() != null) service.setDefaultDurationMinutes(request.getDefaultDurationMinutes());
        if (request.getBasePriceAmount() != null) service.setBasePriceAmount(request.getBasePriceAmount());
        if (request.getImageUrl() != null) service.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) service.setActive(request.getActive());

        if (request.getBookingType() != null) {
            String bt = request.getBookingType().toUpperCase();
            if (!List.of("SHORT", "LONG").contains(bt)) {
                throw new BadRequestException("Loại booking không hợp lệ. Chỉ chấp nhận SHORT hoặc LONG.");
            }
            service.setBookingType(bt);
        }

        if (request.getCategoryIds() != null) {
            List<ServiceCategory> categories = serviceCategoryRepository.findAllById(request.getCategoryIds());
            if (categories.isEmpty()) {
                throw new BadRequestException("Không tìm thấy danh mục nào.");
            }
            service.setCategories(categories);
        }

        CatalogService saved = catalogServiceRepository.save(service);
        if (request.getPriceTiers() != null) {
            savePriceTiers(saved, request.getPriceTiers());
        }
        return toResponse(catalogServiceRepository.findById(saved.getId()).orElse(saved));
    }

    private CatalogServiceResponse toResponse(CatalogService s) {
        return toResponse(s, Set.of(), Map.of());
    }

    private CatalogServiceResponse toResponse(CatalogService s, Set<Long> bookableIds, Map<Long, List<Long>> serviceAreaIds) {
        List<CatalogServiceResponse.CategoryInfo> categoryInfos = s.getCategories() != null
                ? s.getCategories().stream()
                    .map(c -> new CatalogServiceResponse.CategoryInfo(c.getId(), c.getName()))
                    .toList()
                : List.of();
        CatalogServiceResponse.CategoryInfo first = categoryInfos.isEmpty() ? null : categoryInfos.get(0);

        List<PriceTierDTO> tiers = servicePriceTierRepository.findByServiceIdOrderBySpeciesAscWeightFromAsc(s.getId())
                .stream().map(t -> new PriceTierDTO(t.getId(), t.getSpecies(), t.getWeightFrom(), t.getWeightTo(), t.getPriceAmount()))
                .toList();

        return CatalogServiceResponse.builder()
                .id(s.getId())
                .serviceCode(s.getServiceCode())
                .name(s.getName())
                .slug(s.getSlug())
                .shortDescription(s.getShortDescription())
                .description(s.getDescription())
                .defaultDurationMinutes(s.getDefaultDurationMinutes())
                .basePriceAmount(s.getBasePriceAmount())
                .currencyCode(s.getCurrencyCode())
                .priceUnit(s.getPriceUnit())
                .imageUrl(s.getImageUrl())
                .active(s.getActive())
                .bookingType(s.getBookingType())
                .bookable(bookableIds.contains(s.getId()))
                .areaIds(serviceAreaIds.getOrDefault(s.getId(), List.of()))
                .categoryId(first != null ? first.id() : null)
                .categoryName(first != null ? first.name() : null)
                .categories(categoryInfos)
                .priceTiers(tiers)
                .averageRating(s.getAverageRating())
                .totalReviews(s.getTotalReviews())
                .build();
    }

    private void savePriceTiers(CatalogService service, List<PriceTierDTO> tiers) {
        if (tiers == null) return;
        servicePriceTierRepository.deleteByServiceId(service.getId());
        for (PriceTierDTO dto : tiers) {
            ServicePriceTier tier = new ServicePriceTier();
            tier.setService(service);
            tier.setSpecies(dto.species());
            tier.setWeightFrom(dto.weightFrom());
            tier.setWeightTo(dto.weightTo());
            tier.setPriceAmount(dto.priceAmount());
            servicePriceTierRepository.save(tier);
        }
    }
}
