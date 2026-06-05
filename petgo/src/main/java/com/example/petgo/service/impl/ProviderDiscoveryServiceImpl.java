package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.ProviderAvailabilitySlot;
import com.example.petgo.entity.ProviderBusinessHour;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.repository.*;
import com.example.petgo.service.ProviderDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProviderDiscoveryServiceImpl implements ProviderDiscoveryService {

    private static final String DISTANCE_FALLBACK = "--";
    private static final DateTimeFormatter SLOT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderAvailabilitySlotRepository providerAvailabilitySlotRepository;
    private final ProviderBusinessHourRepository providerBusinessHourRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Value("${app.providers.slot-lookahead-days:7}")
    private int slotLookaheadDays;

    @Value("${app.providers.card-slot-limit:3}")
    private int cardSlotLimit;

    @Override
    public ProviderListResponse findProviders(ProviderSearchCriteria criteria) {
        ProviderSearchCriteria safeCriteria = normalizeCriteria(criteria);
        List<ProviderProfile> providers = providerProfileRepository.findActiveProviders();
        if (providers.isEmpty()) {
            return emptyResponse(safeCriteria);
        }

        List<Long> providerIds = providers.stream().map(ProviderProfile::getId).toList();
        Map<Long, List<ProviderService>> providerServicesMap = providerServiceRepository
                .findActiveByProviderIds(providerIds).stream()
                .collect(
                        Collectors.groupingBy(ps -> ps.getProvider().getId(), LinkedHashMap::new, Collectors.toList()));

        LocalDate today = LocalDate.now(APP_ZONE);
        LocalDate toDate = today.plusDays(Math.max(slotLookaheadDays, 1));
        Map<Long, List<ProviderAvailabilitySlot>> providerSlotsMap = providerAvailabilitySlotRepository
                .findUpcomingAvailableSlots(providerIds, today, toDate).stream()
                .collect(Collectors.groupingBy(slot -> slot.getProvider().getId(), LinkedHashMap::new,
                        Collectors.toList()));

        Map<Long, List<ProviderBusinessHour>> providerHoursMap = providerBusinessHourRepository
                .findByProvider_IdIn(providerIds).stream()
                .collect(Collectors.groupingBy(hour -> hour.getProvider().getId(), LinkedHashMap::new,
                        Collectors.toList()));

        List<ProviderView> filteredViews = providers.stream()
                .map(provider -> toProviderView(provider,
                        providerServicesMap.getOrDefault(provider.getId(), List.of()),
                        providerSlotsMap.getOrDefault(provider.getId(), List.of()),
                        providerHoursMap.getOrDefault(provider.getId(), List.of()),
                        safeCriteria.latitude(), safeCriteria.longitude()))
                .filter(view -> matchesFilters(view, safeCriteria))
                .sorted(buildComparator(safeCriteria.sortBy()))
                .toList();

        int totalItems = filteredViews.size();
        int fromIndex = Math.min(safeCriteria.page() * safeCriteria.size(), totalItems);
        int toIndex = Math.min(fromIndex + safeCriteria.size(), totalItems);
        List<ProviderCardResponse> pageItems = filteredViews.subList(fromIndex, toIndex).stream()
                .map(this::mapCard)
                .toList();

        return ProviderListResponse.builder()
                .items(pageItems)
                .totalItems(totalItems)
                .page(safeCriteria.page())
                .size(safeCriteria.size())
                .hasNext(toIndex < totalItems)
                .appliedFilters(mapAppliedFilters(safeCriteria))
                .filterOptions(getFilterOptions())
                .build();
    }

    @Override
    public ProviderFilterOptionsResponse getFilterOptions() {
        List<ServiceCategory> allCategories = serviceCategoryRepository.findByActiveTrueOrderByNameAscIdAsc();
        List<ProviderCategoryOptionResponse> categories = buildProviderCategoryTree(allCategories, null);

        List<String> cities = providerProfileRepository.findDistinctActiveCities();

        return ProviderFilterOptionsResponse.builder()
                .serviceCategories(categories)
                .cities(cities)
                .sortOptions(List.of("FEATURED", "NEAREST", "TOP_RATED", "LOWEST_PRICE"))
                .timeOfDayOptions(List.of("MORNING", "NOON", "AFTERNOON", "EVENING"))
                .build();
    }

    private List<ProviderCategoryOptionResponse> buildProviderCategoryTree(List<ServiceCategory> categories,
            Long parentId) {
        return categories.stream()
                .filter(category -> Objects.equals(parentIdOf(category), parentId))
                .map(category -> ProviderCategoryOptionResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .parentId(parentIdOf(category))
                        .description(category.getDescription())
                        .children(buildProviderCategoryTree(categories, category.getId()))
                        .build())
                .toList();
    }

    private Long parentIdOf(ServiceCategory category) {
        return category.getParent() != null ? category.getParent().getId() : null;
    }

    private ProviderSearchCriteria normalizeCriteria(ProviderSearchCriteria criteria) {
        int safePage = criteria.page() == null || criteria.page() < 0 ? 0 : criteria.page();
        int safeSize = criteria.size() == null || criteria.size() <= 0 ? 12 : Math.min(criteria.size(), 50);
        String sortBy = criteria.sortBy() == null || criteria.sortBy().isBlank() ? "FEATURED"
                : criteria.sortBy().trim().toUpperCase(Locale.ROOT);
        return ProviderSearchCriteria.builder()
                .query(normalize(criteria.query()))
                .city(normalize(criteria.city()))
                .serviceCategoryIds(criteria.serviceCategoryIds() == null ? List.of()
                        : criteria.serviceCategoryIds().stream()
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList())
                .minPrice(criteria.minPrice())
                .maxPrice(criteria.maxPrice())
                .minRating(criteria.minRating())
                .latitude(criteria.latitude())
                .longitude(criteria.longitude())
                .maxDistanceKm(criteria.maxDistanceKm())
                .timeOfDay(normalizeTimeOfDay(criteria.timeOfDay()))
                .sortBy(sortBy)
                .featuredOnly(Boolean.TRUE.equals(criteria.featuredOnly()))
                .page(safePage)
                .size(safeSize)
                .build();
    }

    private ProviderView toProviderView(
            ProviderProfile provider,
            List<ProviderService> providerServices,
            List<ProviderAvailabilitySlot> slots,
            List<ProviderBusinessHour> businessHours,
            Double userLat,
            Double userLng) {
        BigDecimal lowestPrice = resolveLowestPrice(provider, providerServices);
        String currencyCode = resolveCurrencyCode(provider, providerServices);
        List<Long> categoryIds = providerServices.stream()
                .flatMap(service -> collectCategoryIds(service.getService().getCategory()).stream())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<String> categoryNames = providerServices.stream()
                .map(service -> service.getService().getCategory().getName())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        String featuredService = resolveFeaturedService(providerServices);
        List<String> slotLabels = slots.stream()
                .limit(cardSlotLimit)
                .map(slot -> slot.getStartTime().format(SLOT_FORMATTER))
                .toList();

        Double distanceKm = calculateDistanceKm(userLat, userLng, provider.getLatitude(), provider.getLongitude());
        boolean openNow = isOpenNow(businessHours, LocalDateTime.now(APP_ZONE).toLocalTime(),
                LocalDate.now(APP_ZONE).getDayOfWeek());
        String searchableText = buildSearchableText(provider, providerServices, categoryNames);

        return ProviderView.builder()
                .id(provider.getId())
                .name(provider.getBusinessName())
                .slug(provider.getSlug())
                .headline(provider.getHeadline())
                .providerType(provider.getProviderType())
                .rating(provider.getAverageRating())
                .totalReviews(provider.getTotalReviews())
                .address(buildAddress(provider))
                .city(provider.getCity())
                .province(provider.getProvince())
                .priceFrom(lowestPrice)
                .currencyCode(currencyCode)
                .distanceKm(distanceKm)
                .image(resolveProviderImage(provider))
                .featuredService(featuredService)
                .availableSlots(slotLabels)
                .availableSlotTimes(slots.stream().map(ProviderAvailabilitySlot::getStartTime).toList())
                .hot(Boolean.TRUE.equals(provider.getHot()))
                .featured(Boolean.TRUE.equals(provider.getFeatured()))
                .instantBooking(Boolean.TRUE.equals(provider.getAcceptsInstantBooking()))
                .openNow(openNow)
                .verificationStatus(provider.getVerificationStatus())
                .categoryIds(categoryIds)
                .categoryNames(categoryNames)
                .searchableText(searchableText)
                .build();
    }

    private boolean matchesFilters(ProviderView view, ProviderSearchCriteria criteria) {
        if (criteria.query() != null && !view.searchableText().contains(criteria.query())) {
            return false;
        }
        if (criteria.city() != null) {
            String cityValue = Optional.ofNullable(view.city()).orElse("").toLowerCase(Locale.ROOT);
            String provinceValue = Optional.ofNullable(view.province()).orElse("").toLowerCase(Locale.ROOT);
            if (!cityValue.contains(criteria.city()) && !provinceValue.contains(criteria.city())) {
                return false;
            }
        }
        if (!criteria.serviceCategoryIds().isEmpty()
                && Collections.disjoint(view.categoryIds(), criteria.serviceCategoryIds())) {
            return false;
        }
        if (criteria.minPrice() != null
                && (view.priceFrom() == null || view.priceFrom().compareTo(criteria.minPrice()) < 0)) {
            return false;
        }
        if (criteria.maxPrice() != null
                && (view.priceFrom() == null || view.priceFrom().compareTo(criteria.maxPrice()) > 0)) {
            return false;
        }
        if (criteria.minRating() != null
                && (view.rating() == null || view.rating().compareTo(criteria.minRating()) < 0)) {
            return false;
        }
        if (criteria.maxDistanceKm() != null) {
            if (view.distanceKm() == null || view.distanceKm() > criteria.maxDistanceKm()) {
                return false;
            }
        }
        if (criteria.featuredOnly() && !view.featured()) {
            return false;
        }
        if (criteria.timeOfDay() != null
                && view.availableSlotTimes().stream().noneMatch(time -> matchesTimeOfDay(time, criteria.timeOfDay()))) {
            return false;
        }
        return true;
    }

    private Comparator<ProviderView> buildComparator(String sortBy) {
        Comparator<ProviderView> featuredComparator = Comparator
                .comparing((ProviderView view) -> Boolean.TRUE.equals(view.featured())).reversed()
                .thenComparing((ProviderView view) -> Boolean.TRUE.equals(view.hot()), Comparator.reverseOrder());
        Comparator<ProviderView> ratingComparator = Comparator
                .comparing(ProviderView::rating, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(ProviderView::totalReviews, Comparator.nullsLast(Comparator.reverseOrder()));
        Comparator<ProviderView> priceComparator = Comparator.comparing(ProviderView::priceFrom,
                Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<ProviderView> distanceComparator = Comparator.comparing(ProviderView::distanceKm,
                Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<ProviderView> fallback = Comparator.comparing(ProviderView::id);

        return switch (sortBy) {
            case "NEAREST" -> distanceComparator.thenComparing(ratingComparator).thenComparing(fallback);
            case "TOP_RATED" -> ratingComparator.thenComparing(featuredComparator).thenComparing(fallback);
            case "LOWEST_PRICE" -> priceComparator.thenComparing(ratingComparator).thenComparing(fallback);
            case "FEATURED" -> featuredComparator.thenComparing(ratingComparator).thenComparing(distanceComparator)
                    .thenComparing(fallback);
            default -> featuredComparator.thenComparing(ratingComparator).thenComparing(fallback);
        };
    }

    private ProviderCardResponse mapCard(ProviderView view) {
        return ProviderCardResponse.builder()
                .id(view.id())
                .name(view.name())
                .slug(view.slug())
                .headline(view.headline())
                .providerType(view.providerType())
                .rating(view.rating())
                .totalReviews(view.totalReviews())
                .address(view.address())
                .city(view.city())
                .province(view.province())
                .priceFrom(view.priceFrom())
                .currencyCode(view.currencyCode())
                .distance(formatDistance(view.distanceKm()))
                .distanceKm(view.distanceKm())
                .image(view.image())
                .featuredService(view.featuredService())
                .availableSlots(view.availableSlots())
                .hot(view.hot())
                .featured(view.featured())
                .instantBooking(view.instantBooking())
                .openNow(view.openNow())
                .verificationStatus(view.verificationStatus())
                .categoryIds(view.categoryIds())
                .categoryNames(view.categoryNames())
                .build();
    }

    private ProviderAppliedFiltersResponse mapAppliedFilters(ProviderSearchCriteria criteria) {
        return ProviderAppliedFiltersResponse.builder()
                .query(criteria.query())
                .city(criteria.city())
                .serviceCategoryIds(criteria.serviceCategoryIds())
                .minPrice(criteria.minPrice())
                .maxPrice(criteria.maxPrice())
                .minRating(criteria.minRating())
                .latitude(criteria.latitude())
                .longitude(criteria.longitude())
                .maxDistanceKm(criteria.maxDistanceKm())
                .timeOfDay(criteria.timeOfDay())
                .sortBy(criteria.sortBy())
                .featuredOnly(criteria.featuredOnly())
                .build();
    }

    private ProviderListResponse emptyResponse(ProviderSearchCriteria criteria) {
        return ProviderListResponse.builder()
                .items(List.of())
                .totalItems(0)
                .page(criteria.page())
                .size(criteria.size())
                .hasNext(false)
                .appliedFilters(mapAppliedFilters(criteria))
                .filterOptions(getFilterOptions())
                .build();
    }

    private BigDecimal resolveLowestPrice(ProviderProfile provider, List<ProviderService> providerServices) {
        if (provider.getPriceFromAmount() != null) {
            return provider.getPriceFromAmount();
        }
        return providerServices.stream()
                .map(ProviderService::getPriceAmount)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private String resolveCurrencyCode(ProviderProfile provider, List<ProviderService> providerServices) {
        if (provider.getCurrencyCode() != null && !provider.getCurrencyCode().isBlank()) {
            return provider.getCurrencyCode();
        }
        return providerServices.stream()
                .map(ProviderService::getCurrencyCode)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse("VND");
    }

    private String resolveFeaturedService(List<ProviderService> providerServices) {
        return providerServices.stream()
                .sorted(Comparator
                        .comparing(
                                (ProviderService providerService) -> Boolean.TRUE.equals(providerService.getFeatured()))
                        .reversed()
                        .thenComparing(ProviderService::getDisplayOrder)
                        .thenComparing(ProviderService::getId))
                .map(this::resolveServiceDisplayName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Pet care service");
    }

    private String resolveServiceDisplayName(ProviderService providerService) {
        if (providerService.getCustomName() != null && !providerService.getCustomName().isBlank()) {
            return providerService.getCustomName();
        }
        if (providerService.getService() != null && providerService.getService().getName() != null
                && !providerService.getService().getName().isBlank()) {
            return providerService.getService().getName();
        }
        return providerService.getShortDescription();
    }

    private String buildAddress(ProviderProfile provider) {
        return Stream
                .of(provider.getPrimaryAddressLine1(), provider.getDistrict(), provider.getCity(),
                        provider.getProvince())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private String buildSearchableText(ProviderProfile provider, List<ProviderService> providerServices,
            List<String> categoryNames) {
        List<String> serviceTexts = providerServices.stream()
                .flatMap(providerService -> Stream.of(
                        providerService.getCustomName(),
                        providerService.getShortDescription(),
                        providerService.getDescription(),
                        providerService.getService() != null ? providerService.getService().getName() : null,
                        providerService.getService() != null ? providerService.getService().getShortDescription()
                                : null,
                        providerService.getService() != null && providerService.getService().getCategory() != null
                                ? providerService.getService().getCategory().getName()
                                : null))
                .filter(Objects::nonNull)
                .toList();

        return Stream.concat(
                Stream.of(provider.getBusinessName(), provider.getHeadline(), provider.getDescription(),
                        provider.getCity(), provider.getProvince(), provider.getDistrict()),
                Stream.concat(serviceTexts.stream(), categoryNames.stream()))
                .filter(Objects::nonNull)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" | "));
    }

    private List<Long> collectCategoryIds(ServiceCategory category) {
        List<Long> ids = new ArrayList<>();
        ServiceCategory cursor = category;
        while (cursor != null) {
            ids.add(cursor.getId());
            cursor = cursor.getParent();
        }
        return ids;
    }

    private String resolveProviderImage(ProviderProfile provider) {
        if (provider.getMainImageUrl() != null && !provider.getMainImageUrl().isBlank()) {
            return provider.getMainImageUrl();
        }
        if (provider.getCoverImageUrl() != null && !provider.getCoverImageUrl().isBlank()) {
            return provider.getCoverImageUrl();
        }
        return "https://placehold.co/800x600?text=PetGo+Provider";
    }

    private String formatDistance(Double distanceKm) {
        if (distanceKm == null) {
            return DISTANCE_FALLBACK;
        }
        return BigDecimal.valueOf(distanceKm)
                .setScale(1, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString() + " km";
    }

    private Double calculateDistanceKm(Double userLat, Double userLng, BigDecimal providerLat, BigDecimal providerLng) {
        if (userLat == null || userLng == null || providerLat == null || providerLng == null) {
            return null;
        }

        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(providerLat.doubleValue() - userLat);
        double dLng = Math.toRadians(providerLng.doubleValue() - userLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(providerLat.doubleValue()))
                        * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private boolean isOpenNow(List<ProviderBusinessHour> businessHours, LocalTime now, DayOfWeek dayOfWeek) {
        int weekday = dayOfWeek.getValue();
        return businessHours.stream()
                .filter(hour -> Objects.equals(hour.getWeekday(), weekday))
                .filter(hour -> !Boolean.TRUE.equals(hour.getClosed()))
                .anyMatch(hour -> {
                    if (hour.getOpensAt() == null || hour.getClosesAt() == null) {
                        return false;
                    }
                    boolean insideMainWindow = !now.isBefore(hour.getOpensAt()) && now.isBefore(hour.getClosesAt());
                    if (!insideMainWindow) {
                        return false;
                    }
                    if (hour.getBreakStartsAt() != null && hour.getBreakEndsAt() != null) {
                        return now.isBefore(hour.getBreakStartsAt()) || !now.isBefore(hour.getBreakEndsAt());
                    }
                    return true;
                });
    }

    private boolean matchesTimeOfDay(LocalTime time, String timeOfDay) {
        return switch (timeOfDay) {
            case "MORNING" -> !time.isBefore(LocalTime.of(5, 0)) && time.isBefore(LocalTime.of(11, 0));
            case "NOON" -> !time.isBefore(LocalTime.of(11, 0)) && time.isBefore(LocalTime.of(14, 0));
            case "AFTERNOON" -> !time.isBefore(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(18, 0));
            case "EVENING" -> !time.isBefore(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(23, 0));
            default -> true;
        };
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeTimeOfDay(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "SANG", "MORNING" -> "MORNING";
            case "TRUA", "NOON" -> "NOON";
            case "CHIEU", "AFTERNOON" -> "AFTERNOON";
            case "TOI", "EVENING" -> "EVENING";
            default -> normalized;
        };
    }

    private record ProviderView(
            Long id,
            String name,
            String slug,
            String headline,
            String providerType,
            BigDecimal rating,
            Integer totalReviews,
            String address,
            String city,
            String province,
            BigDecimal priceFrom,
            String currencyCode,
            Double distanceKm,
            String image,
            String featuredService,
            List<String> availableSlots,
            List<LocalTime> availableSlotTimes,
            Boolean hot,
            Boolean featured,
            Boolean instantBooking,
            Boolean openNow,
            String verificationStatus,
            List<Long> categoryIds,
            List<String> categoryNames,
            String searchableText) {
        public static Builder builder() {
            return new Builder();
        }

        private static final class Builder {
            private Long id;
            private String name;
            private String slug;
            private String headline;
            private String providerType;
            private BigDecimal rating;
            private Integer totalReviews;
            private String address;
            private String city;
            private String province;
            private BigDecimal priceFrom;
            private String currencyCode;
            private Double distanceKm;
            private String image;
            private String featuredService;
            private List<String> availableSlots = List.of();
            private List<LocalTime> availableSlotTimes = List.of();
            private Boolean hot;
            private Boolean featured;
            private Boolean instantBooking;
            private Boolean openNow;
            private String verificationStatus;
            private List<Long> categoryIds = List.of();
            private List<String> categoryNames = List.of();
            private String searchableText;

            private Builder id(Long id) {
                this.id = id;
                return this;
            }

            private Builder name(String name) {
                this.name = name;
                return this;
            }

            private Builder slug(String slug) {
                this.slug = slug;
                return this;
            }

            private Builder headline(String headline) {
                this.headline = headline;
                return this;
            }

            private Builder providerType(String providerType) {
                this.providerType = providerType;
                return this;
            }

            private Builder rating(BigDecimal rating) {
                this.rating = rating;
                return this;
            }

            private Builder totalReviews(Integer totalReviews) {
                this.totalReviews = totalReviews;
                return this;
            }

            private Builder address(String address) {
                this.address = address;
                return this;
            }

            private Builder city(String city) {
                this.city = city;
                return this;
            }

            private Builder province(String province) {
                this.province = province;
                return this;
            }

            private Builder priceFrom(BigDecimal priceFrom) {
                this.priceFrom = priceFrom;
                return this;
            }

            private Builder currencyCode(String currencyCode) {
                this.currencyCode = currencyCode;
                return this;
            }

            private Builder distanceKm(Double distanceKm) {
                this.distanceKm = distanceKm;
                return this;
            }

            private Builder image(String image) {
                this.image = image;
                return this;
            }

            private Builder featuredService(String featuredService) {
                this.featuredService = featuredService;
                return this;
            }

            private Builder availableSlots(List<String> availableSlots) {
                this.availableSlots = availableSlots;
                return this;
            }

            private Builder availableSlotTimes(List<LocalTime> availableSlotTimes) {
                this.availableSlotTimes = availableSlotTimes;
                return this;
            }

            private Builder hot(Boolean hot) {
                this.hot = hot;
                return this;
            }

            private Builder featured(Boolean featured) {
                this.featured = featured;
                return this;
            }

            private Builder instantBooking(Boolean instantBooking) {
                this.instantBooking = instantBooking;
                return this;
            }

            private Builder openNow(Boolean openNow) {
                this.openNow = openNow;
                return this;
            }

            private Builder verificationStatus(String verificationStatus) {
                this.verificationStatus = verificationStatus;
                return this;
            }

            private Builder categoryIds(List<Long> categoryIds) {
                this.categoryIds = categoryIds;
                return this;
            }

            private Builder categoryNames(List<String> categoryNames) {
                this.categoryNames = categoryNames;
                return this;
            }

            private Builder searchableText(String searchableText) {
                this.searchableText = searchableText;
                return this;
            }

            private ProviderView build() {
                return new ProviderView(id, name, slug, headline, providerType, rating, totalReviews, address, city,
                        province,
                        priceFrom, currencyCode, distanceKm, image, featuredService, availableSlots, availableSlotTimes,
                        hot, featured, instantBooking, openNow, verificationStatus, categoryIds, categoryNames,
                        searchableText);
            }
        }
    }
}
