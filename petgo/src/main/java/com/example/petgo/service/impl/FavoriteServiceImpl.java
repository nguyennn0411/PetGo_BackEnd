package com.example.petgo.service.impl;

import com.example.petgo.dto.FavoriteListResponse;
import com.example.petgo.dto.FavoriteMutationResponse;
import com.example.petgo.dto.FavoriteProviderResponse;
import com.example.petgo.entity.Favorite;
import com.example.petgo.entity.ProviderBusinessHour;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter SAVED_AT_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderBusinessHourRepository providerBusinessHourRepository;

    @Override
    @Transactional(readOnly = true)
    public FavoriteListResponse getFavorites(Long ownerUserId, Double latitude, Double longitude) {
        userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        List<Favorite> favorites = favoriteRepository.findByUser_IdAndProvider_StatusAndProvider_DeletedAtIsNullOrderByCreatedAtDescIdDesc(ownerUserId, "ACTIVE");
        if (favorites.isEmpty()) {
            return FavoriteListResponse.builder()
                    .ownerUserId(ownerUserId)
                    .favoriteProviderIds(List.of())
                    .items(List.of())
                    .totalItems(0)
                    .build();
        }

        List<Long> providerIds = favorites.stream()
                .map(favorite -> favorite.getProvider().getId())
                .distinct()
                .toList();

        Map<Long, List<ProviderService>> servicesMap = providerServiceRepository.findActiveByProviderIds(providerIds).stream()
                .collect(Collectors.groupingBy(ps -> ps.getProvider().getId(), LinkedHashMap::new, Collectors.toList()));

        Map<Long, List<ProviderBusinessHour>> hoursMap = providerBusinessHourRepository.findByProvider_IdIn(providerIds).stream()
                .collect(Collectors.groupingBy(hour -> hour.getProvider().getId(), LinkedHashMap::new, Collectors.toList()));

        List<FavoriteProviderResponse> items = favorites.stream()
                .map(favorite -> mapItem(
                        favorite,
                        servicesMap.getOrDefault(favorite.getProvider().getId(), List.of()),
                        hoursMap.getOrDefault(favorite.getProvider().getId(), List.of()),
                        latitude,
                        longitude))
                .toList();

        return FavoriteListResponse.builder()
                .ownerUserId(ownerUserId)
                .favoriteProviderIds(providerIds)
                .items(items)
                .totalItems(items.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getFavoriteProviderIds(Long ownerUserId) {
        userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        return favoriteRepository.findByUser_IdOrderByCreatedAtDescIdDesc(ownerUserId).stream()
                .map(favorite -> favorite.getProvider().getId())
                .distinct()
                .toList();
    }

    @Override
    @Transactional
    public FavoriteMutationResponse addFavorite(Long ownerUserId, Long providerId) {
        var user = userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        ProviderProfile provider = providerProfileRepository.findActiveById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));

        if (favoriteRepository.existsByUser_IdAndProvider_Id(ownerUserId, providerId)) {
            return FavoriteMutationResponse.builder()
                    .ownerUserId(ownerUserId)
                    .providerId(providerId)
                    .favorite(true)
                    .message("Nhà cung cấp đã nằm trong danh sách yêu thích")
                    .build();
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProvider(provider);
        favoriteRepository.save(favorite);

        return FavoriteMutationResponse.builder()
                .ownerUserId(ownerUserId)
                .providerId(providerId)
                .favorite(true)
                .message("Đã thêm vào danh sách yêu thích")
                .build();
    }

    @Override
    @Transactional
    public FavoriteMutationResponse removeFavorite(Long ownerUserId, Long providerId) {
        userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!favoriteRepository.existsByUser_IdAndProvider_Id(ownerUserId, providerId)) {
            return FavoriteMutationResponse.builder()
                    .ownerUserId(ownerUserId)
                    .providerId(providerId)
                    .favorite(false)
                    .message("Nhà cung cấp chưa nằm trong danh sách yêu thích")
                    .build();
        }

        favoriteRepository.deleteByUser_IdAndProvider_Id(ownerUserId, providerId);

        return FavoriteMutationResponse.builder()
                .ownerUserId(ownerUserId)
                .providerId(providerId)
                .favorite(false)
                .message("Đã xóa khỏi danh sách yêu thích")
                .build();
    }

    private FavoriteProviderResponse mapItem(
            Favorite favorite,
            List<ProviderService> providerServices,
            List<ProviderBusinessHour> businessHours,
            Double userLat,
            Double userLng
    ) {
        ProviderProfile provider = favorite.getProvider();
        Double distanceKm = calculateDistanceKm(userLat, userLng, toDouble(provider.getLatitude()), toDouble(provider.getLongitude()));
        BigDecimal priceFrom = resolveLowestPrice(provider, providerServices);
        String featuredService = resolveFeaturedService(providerServices);
        List<String> categorySlugs = providerServices.stream()
                .map(ps -> ps.getService().getCategory().getSlug())
                .filter(Objects::nonNull)
                .map(slug -> slug.toLowerCase(Locale.ROOT))
                .distinct()
                .toList();

        return FavoriteProviderResponse.builder()
                .providerId(provider.getId())
                .name(provider.getBusinessName())
                .headline(provider.getHeadline())
                .rating(provider.getAverageRating())
                .totalReviews(provider.getTotalReviews())
                .address(buildAddress(provider))
                .priceFrom(priceFrom)
                .priceFromDisplay(formatMoney(priceFrom))
                .currencyCode(firstNonBlank(provider.getCurrencyCode(), "VND"))
                .image(firstNonBlank(provider.getMainImageUrl(), provider.getCoverImageUrl()))
                .featuredService(featuredService)
                .distance(formatDistance(distanceKm))
                .distanceKm(distanceKm)
                .featured(Boolean.TRUE.equals(provider.getFeatured()))
                .hot(Boolean.TRUE.equals(provider.getHot()))
                .instantBooking(Boolean.TRUE.equals(provider.getAcceptsInstantBooking()))
                .openNow(isOpenNow(businessHours))
                .verificationStatus(provider.getVerificationStatus())
                .categorySlugs(categorySlugs)
                .savedAt(favorite.getCreatedAt() != null ? favorite.getCreatedAt().atZone(APP_ZONE).format(SAVED_AT_VIEW) : null)
                .build();
    }

    private BigDecimal resolveLowestPrice(ProviderProfile provider, List<ProviderService> services) {
        return services.stream()
                .map(ProviderService::getPriceAmount)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(provider.getPriceFromAmount());
    }

    private String resolveFeaturedService(List<ProviderService> services) {
        return services.stream()
                .findFirst()
                .map(service -> firstNonBlank(service.getCustomName(), service.getService() != null ? service.getService().getName() : null))
                .orElse("Dịch vụ thú cưng");
    }

    private boolean isOpenNow(List<ProviderBusinessHour> businessHours) {
        if (businessHours == null || businessHours.isEmpty()) return false;
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        int weekday = now.getDayOfWeek().getValue();
        LocalTime currentTime = now.toLocalTime();

        return businessHours.stream()
                .filter(hour -> Objects.equals(hour.getWeekday(), weekday))
                .findFirst()
                .filter(hour -> !Boolean.TRUE.equals(hour.getClosed()))
                .filter(hour -> hour.getOpensAt() != null && hour.getClosesAt() != null)
                .filter(hour -> !currentTime.isBefore(hour.getOpensAt()) && !currentTime.isAfter(hour.getClosesAt()))
                .isPresent();
    }

    private Double calculateDistanceKm(Double userLat, Double userLng, Double providerLat, Double providerLng) {
        if (userLat == null || userLng == null || providerLat == null || providerLng == null) return null;
        double earthRadiusKm = 6371.0d;
        double dLat = Math.toRadians(providerLat - userLat);
        double dLng = Math.toRadians(providerLng - userLng);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(providerLat))
                * Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return BigDecimal.valueOf(earthRadiusKm * c).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private String formatDistance(Double distanceKm) {
        if (distanceKm == null) return "--";
        return distanceKm % 1 == 0
                ? String.format(Locale.US, "%.0f km", distanceKm)
                : String.format(Locale.US, "%.1f km", distanceKm);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "Liên hệ";
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", value);
    }

    private String buildAddress(ProviderProfile provider) {
        List<String> parts = new ArrayList<>();
        if (provider == null) return "";
        if (notBlank(provider.getPrimaryAddressLine1())) parts.add(provider.getPrimaryAddressLine1().trim());
        if (notBlank(provider.getWard())) parts.add(provider.getWard().trim());
        if (notBlank(provider.getDistrict())) parts.add(provider.getDistrict().trim());
        if (notBlank(provider.getCity())) parts.add(provider.getCity().trim());
        if (parts.isEmpty() && notBlank(provider.getProvince())) parts.add(provider.getProvince().trim());
        return String.join(", ", parts);
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }

    private Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
