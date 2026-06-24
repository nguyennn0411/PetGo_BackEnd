package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
public class PromoCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_type", length = 20)
    private String ownerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "promotion_type", length = 30)
    private String promotionType;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_order_amount", precision = 12, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "usage_limit_total")
    private Integer usageLimitTotal;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser;

    @Column(name = "usage_count")
    private Integer usageCount;

    @Column(name = "is_stackable")
    private Boolean stackable;

    @Column(name = "is_auto_apply")
    private Boolean autoApply;

    @Column
    private Integer priority;

    @Column(name = "user_segment", length = 30)
    private String userSegment;

    @Column(name = "min_completed_bookings")
    private Integer minCompletedBookings;

    @Column(name = "applicable_days_of_week", length = 100)
    private String applicableDaysOfWeek;

    @Column(name = "area_ids", length = 1000)
    private String areaIds;

    @Column(name = "service_category_ids", length = 1000)
    private String serviceCategoryIds;

    @Column(name = "membership_plan_ids", length = 1000)
    private String membershipPlanIds;

    @Column(name = "badge_text", length = 80)
    private String badgeText;

    @Column(name = "landing_page_url", length = 500)
    private String landingPageUrl;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "internal_note", columnDefinition = "TEXT")
    private String internalNote;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
