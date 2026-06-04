package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerProfileResponse;
import com.example.petgo.dto.partner.PartnerProfileUpdateRequest;
import com.example.petgo.entity.ProviderPhoto;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.RegistrationApplication;
import com.example.petgo.entity.RegistrationType;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.repository.ProviderPhotoRepository;
import com.example.petgo.repository.RegistrationApplicationRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import com.example.petgo.service.impl.RegistrationMapperSupport;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PartnerProfileServiceImpl implements PartnerProfileService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final RegistrationMapperSupport registrationMapperSupport;
    private final ProviderPhotoRepository providerPhotoRepository;
    private final RegistrationApplicationRepository registrationApplicationRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PartnerProfileResponse getProfile(HttpServletRequest request) {
        PartnerAccessService.PartnerContext context = partnerAccessService.requirePartnerContext(request);
        return buildResponse(context.provider());
    }

    @Override
    @Transactional
    public PartnerProfileResponse updateProfile(HttpServletRequest request, PartnerProfileUpdateRequest requestBody) {
        PartnerAccessService.PartnerContext context = partnerAccessService.requirePartnerContext(request);
        ProviderProfile provider = context.provider();

        String businessName = mapper.normalizeBlank(requestBody.businessName());
        if (businessName != null) {
            provider.setBusinessName(businessName);
        }
        provider.setDescription(mapper.normalizeBlank(requestBody.description()));
        provider.setEmergencyPhone(mapper.normalizeBlank(requestBody.emergencyPhone()));
        provider.setPrimaryAddressLine1(mapper.normalizeBlank(requestBody.primaryAddressLine1()));
        provider.setPrimaryAddressLine2(null);
        provider.setWard(mapper.normalizeBlank(requestBody.ward()));
        provider.setDistrict(mapper.normalizeBlank(requestBody.district()));
        provider.setCity(mapper.normalizeBlank(requestBody.city()));
        provider.setProvince(null);
        if (requestBody.latitude() != null) {
            provider.setLatitude(requestBody.latitude());
        }
        if (requestBody.longitude() != null) {
            provider.setLongitude(requestBody.longitude());
        }
        provider.setMainImageUrl(mapper.normalizeBlank(requestBody.mainImageUrl()));
        provider.setCoverImageUrl(mapper.normalizeBlank(requestBody.coverImageUrl()));
        replaceGalleryPhotos(provider, requestBody.photoUrls());

        return buildResponse(provider);
    }

    private void replaceGalleryPhotos(ProviderProfile provider, List<String> rawPhotoUrls) {
        if (rawPhotoUrls == null)
            return;
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        rawPhotoUrls.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(url -> !url.isBlank())
                .limit(10)
                .forEach(normalized::add);

        providerPhotoRepository.deleteByProvider_Id(provider.getId());
        List<ProviderPhoto> photos = new ArrayList<>();
        int sortOrder = 0;
        for (String photoUrl : normalized) {
            ProviderPhoto photo = new ProviderPhoto();
            photo.setProvider(provider);
            photo.setPhotoUrl(photoUrl);
            photo.setMediaType("IMAGE");
            photo.setPrimary(sortOrder == 0);
            photo.setSortOrder(sortOrder++);
            photos.add(photo);
        }
        if (!photos.isEmpty())
            providerPhotoRepository.saveAll(photos);
    }

    private PartnerProfileResponse buildResponse(ProviderProfile provider) {
        RegistrationApplication registration = provider.getUser() == null ? null
                : registrationApplicationRepository
                        .findByUser_IdAndType(provider.getUser().getId(), RegistrationType.PARTNER)
                        .orElse(null);
        List<Long> categoryIds = registration != null
                ? registrationMapperSupport.parseLongCsv(registration.getServiceCategoryIds())
                : List.of();
        List<ServiceCategory> categories = categoryIds.isEmpty() ? List.of()
                : serviceCategoryRepository.findAllById(categoryIds);
        return mapper.mapProfile(
                provider,
                registration,
                providerPhotoRepository.findImagesByProviderId(provider.getId()),
                categories);
    }
}