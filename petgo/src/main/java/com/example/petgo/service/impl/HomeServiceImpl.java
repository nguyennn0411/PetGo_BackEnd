package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.repository.*;
import com.example.petgo.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

        private final ServiceCategoryRepository serviceCategoryRepository;
        private final MembershipPlanRepository membershipPlanRepository;
        private final HomeSliderRepository homeSliderRepository;

        @Value("${app.homepage.membership-limit:3}")
        private int membershipLimit;

        @Override
        public HomePageResponse getHomePage(Double latitude, Double longitude) {
                List<ServiceCategory> categories = serviceCategoryRepository.findByActiveTrueOrderByNameAscIdAsc();
                List<MembershipPlan> membershipPlans = membershipPlanRepository
                                .findByActiveTrueOrderByPopularDescSortOrderAscIdAsc(
                                                PageRequest.of(0, membershipLimit));
                List<HomeSlider> sliders = homeSliderRepository.findByActiveTrueOrderBySortOrderAscIdAsc();

                return HomePageResponse.builder()
                                .sliders(sliders.stream().map(this::mapSlider).toList())
                                .categories(buildCategoryTree(categories, null))
                                .membershipPlans(membershipPlans.stream().map(this::mapMembership).toList())
                                .stats(HomeStatsResponse.builder()
                                                .activeCategories(categories.size())
                                                .activeMembershipPlans(membershipPlans.size())
                                                .build())
                                .build();
        }

        private HomeSliderResponse mapSlider(HomeSlider slider) {
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

        private HomeCategoryResponse mapCategory(ServiceCategory category) {
                return mapCategory(category, List.of());
        }

        private HomeCategoryResponse mapCategory(ServiceCategory category, List<HomeCategoryResponse> children) {
                return HomeCategoryResponse.builder()
                                .id(category.getId())
                                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                                .name(category.getName())
                                .description(category.getDescription())
                                .children(children)
                                .build();
        }

        private List<HomeCategoryResponse> buildCategoryTree(List<ServiceCategory> categories, Long parentId) {
                return categories.stream()
                                .filter(category -> Objects.equals(parentIdOf(category), parentId))
                                .map(category -> mapCategory(category, buildCategoryTree(categories, category.getId())))
                                .toList();
        }

        private Long parentIdOf(ServiceCategory category) {
                return category.getParent() != null ? category.getParent().getId() : null;
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
                                                .sorted(Comparator.comparing(MembershipPlanFeature::getSortOrder)
                                                                .thenComparing(MembershipPlanFeature::getId))
                                                .map(MembershipPlanFeature::getFeatureText)
                                                .limit(6)
                                                .toList())
                                .build();
        }
}