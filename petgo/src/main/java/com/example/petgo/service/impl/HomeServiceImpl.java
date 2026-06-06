package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.repository.MembershipPlanRepository;
import com.example.petgo.repository.ProviderProfileRepository;
import com.example.petgo.repository.ReviewRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import com.example.petgo.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private static final String FALLBACK_DISTANCE = "--";

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final ReviewRepository reviewRepository;

    @Value("${app.homepage.featured-limit:3}")
    private int featuredLimit;

    @Value("${app.homepage.nearby-limit:6}")
    private int nearbyLimit;

    @Value("${app.homepage.review-limit:6}")
    private int reviewLimit;

    @Value("${app.homepage.membership-limit:3}")
    private int membershipLimit;

    @Override
    public HomePageResponse getHomePage(Double latitude, Double longitude) {
        List<ServiceCategory> categories = serviceCategoryRepository.findByActiveTrueOrderBySortOrderAscIdAsc();
        List<ProviderProfile> featuredProviders = providerProfileRepository.findFeaturedProviders(PageRequest.of(0, featuredLimit));
        List<ProviderProfile> nearbyProviders = providerProfileRepository.findNearbyProviders(PageRequest.of(0, nearbyLimit));
        List<MembershipPlan> membershipPlans = membershipPlanRepository.findByActiveTrueOrderByPopularDescSortOrderAscIdAsc(PageRequest.of(0, membershipLimit));
        List<Review> reviews = reviewRepository.findByStatusAndDeletedAtIsNullOrderByCreatedAtDesc("VISIBLE", PageRequest.of(0, reviewLimit));

        List<HomeProviderResponse> mappedNearbyProviders = mapProvidersWithDistance(nearbyProviders, latitude, longitude);
        List<HomeProviderResponse> mappedFeaturedProviders = mapProvidersWithDistance(featuredProviders, latitude, longitude);

        return HomePageResponse.builder()
                .categories(categories.stream().map(this::mapCategory).toList())
                .nearbyProviders(mappedNearbyProviders)
                .featuredProviders(mappedFeaturedProviders)
                .membershipPlans(membershipPlans.stream().map(this::mapMembership).toList())
                .reviews(reviews.stream().map(this::mapReview).toList())
                .stats(HomeStatsResponse.builder()
                        .activeCategories(categories.size())
                        .activeProviders(Math.max(mappedNearbyProviders.size(), mappedFeaturedProviders.size()))
                        .visibleReviews(reviews.size())
                        .activeMembershipPlans(membershipPlans.size())
                        .build())
                .build();
    }

    private List<HomeProviderResponse> mapProvidersWithDistance(List<ProviderProfile> providers, Double userLat, Double userLng) {
        return providers.stream()
                .map(provider -> new ProviderWithDistance(provider, calculateDistanceKm(userLat, userLng, provider.getLatitude(), provider.getLongitude())))
                .sorted(Comparator.comparing(ProviderWithDistance::distanceForSort, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(item -> item.provider().getAverageRating(), Comparator.nullsLast(Comparator.reverseOrder())))
                .map(item -> mapProvider(item.provider(), item.distanceForSort()))
                .toList();
    }

    private HomeCategoryResponse mapCategory(ServiceCategory category) {
        return HomeCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .iconKey(category.getIconKey())
                .description(category.getDescription())
                .build();
    }

    private HomeProviderResponse mapProvider(ProviderProfile provider, Double distanceKm) {
        return HomeProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getBusinessName())
                .slug(provider.getSlug())
                .rating(provider.getAverageRating())
                .totalReviews(provider.getTotalReviews())
                .distance(formatDistance(distanceKm))
                .price(provider.getPriceFromAmount())
                .currencyCode(provider.getCurrencyCode())
                .image(resolveProviderImage(provider))
                .headline(provider.getHeadline())
                .featured(provider.getFeatured())
                .instantBooking(provider.getAcceptsInstantBooking())
                .city(provider.getCity())
                .build();
    }

    private HomeMembershipPlanResponse mapMembership(MembershipPlan plan) {
        return HomeMembershipPlanResponse.builder()
                .id(plan.getId())
                .code(plan.getPlanCode())
                .name(plan.getName())
                .slug(plan.getSlug())
                .price(plan.getPriceAmount())
                .currencyCode(plan.getCurrencyCode())
                .billingCycle(plan.getBillingCycle())
                .discountPercent(plan.getDiscountPercent())
                .monthlyVoucherAmount(plan.getMonthlyVoucherAmount())
                .popular(plan.getPopular())
                .features(plan.getFeatures().stream()
                        .sorted(Comparator.comparing(MembershipPlanFeature::getSortOrder).thenComparing(MembershipPlanFeature::getId))
                        .map(MembershipPlanFeature::getFeatureText)
                        .limit(6)
                        .toList())
                .build();
    }

    private HomeReviewResponse mapReview(Review review) {
        Pet pet = review.getBooking() != null ? review.getBooking().getPet() : null;
        return HomeReviewResponse.builder()
                .id(review.getId())
                .customerName(review.getCustomerUser() != null ? review.getCustomerUser().getFullName() : "Khách hàng PetGo")
                .petLabel(buildPetLabel(pet))
                .rating(review.getRating())
                .text(review.getComment())
                .avatar(review.getCustomerUser() != null ? review.getCustomerUser().getAvatarUrl() : null)
                .build();
    }

    private String buildPetLabel(Pet pet) {
        if (pet == null) {
            return "Thú cưng";
        }
        String species = pet.getSpecies() != null ? pet.getSpecies().toLowerCase(Locale.ROOT) : "pet";
        String breed = pet.getBreed() != null && !pet.getBreed().isBlank() ? pet.getBreed() : species;
        return pet.getName() + " (" + breed + ")";
    }

    private String resolveProviderImage(ProviderProfile provider) {
        if (provider.getMainImageUrl() != null && !provider.getMainImageUrl().isBlank()) {
            return provider.getMainImageUrl();
        }
        if (provider.getCoverImageUrl() != null && !provider.getCoverImageUrl().isBlank()) {
            return provider.getCoverImageUrl();
        }
        return "https://placehold.co/800x600?text=PetGo";
    }

    private String formatDistance(Double distanceKm) {
        if (distanceKm == null) {
            return FALLBACK_DISTANCE;
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

    private record ProviderWithDistance(ProviderProfile provider, Double distanceForSort) {
    }
}
