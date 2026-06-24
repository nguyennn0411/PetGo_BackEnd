package com.example.petgo.dto.promotion;

import lombok.Builder;

import java.util.List;

@Builder
public record PromotionOptionsResponse(
                List<OptionItem> promotionTypes,
                List<OptionItem> targetTypes,
                List<OptionItem> discountTypes,
                List<OptionItem> userSegments,
                List<OptionItem> daysOfWeek,
                List<AreaOption> areas,
                List<ServiceCategoryOption> serviceCategories,
                List<MembershipPlanOption> membershipPlans) {

        @Builder
        public record OptionItem(String value, String label, String description) {
        }

        @Builder
        public record AreaOption(Long id, String name, String pickupAddress) {
        }

        @Builder
        public record ServiceCategoryOption(Long id, String name, Long parentId, String parentName) {
        }

        @Builder
        public record MembershipPlanOption(Long id, String name, String slug, String billingCycle) {
        }
}
