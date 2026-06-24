package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.CatalogServiceResponse;
import com.example.petgo.entity.CatalogService;
import com.example.petgo.entity.UserFavoriteService;
import com.example.petgo.repository.CatalogServiceRepository;
import com.example.petgo.repository.UserFavoriteServiceRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final AuthService authService;
    private final UserFavoriteServiceRepository favoriteRepository;
    private final CatalogServiceRepository catalogServiceRepository;

    @Override
    @Transactional
    public boolean toggleFavorite(HttpServletRequest request, Long serviceId) {
        AuthenticatedUser user = authService.requireAccessUser(request);

        var existing = favoriteRepository.findByUserIdAndServiceId(user.userId(), serviceId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false;
        } else {
            var fav = new UserFavoriteService();
            fav.setUserId(user.userId());
            fav.setServiceId(serviceId);
            favoriteRepository.save(fav);
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> getUserFavorites(HttpServletRequest request) {
        AuthenticatedUser user = authService.requireAccessUser(request);

        return favoriteRepository.findByUserId(user.userId()).stream()
                .map(fav -> catalogServiceRepository.findById(fav.getServiceId()).orElse(null))
                .filter(s -> s != null)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getUserFavoriteIds(HttpServletRequest request) {
        AuthenticatedUser user = authService.requireAccessUser(request);

        return favoriteRepository.findByUserId(user.userId()).stream()
                .map(UserFavoriteService::getServiceId)
                .collect(Collectors.toSet());
    }

    private CatalogServiceResponse toResponse(CatalogService s) {
        List<CatalogServiceResponse.CategoryInfo> categoryInfos = s.getCategories() != null
                ? s.getCategories().stream()
                    .map(c -> new CatalogServiceResponse.CategoryInfo(c.getId(), c.getName()))
                    .toList()
                : List.of();
        CatalogServiceResponse.CategoryInfo first = categoryInfos.isEmpty() ? null : categoryInfos.get(0);
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
                .categoryId(first != null ? first.id() : null)
                .categoryName(first != null ? first.name() : null)
                .categories(categoryInfos)
                .build();
    }
}
