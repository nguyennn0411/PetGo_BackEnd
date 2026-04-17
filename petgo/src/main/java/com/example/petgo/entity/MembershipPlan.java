package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
public class MembershipPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_code", nullable = false, length = 50)
    private String planCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String slug;

    @Column(length = 255)
    private String description;

    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle;

    @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "monthly_voucher_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyVoucherAmount;

    @Column(name = "priority_booking", nullable = false)
    private Boolean priorityBooking;

    @Column(name = "priority_support", nullable = false)
    private Boolean prioritySupport;

    @Column(name = "is_popular", nullable = false)
    private Boolean popular;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "membershipPlan", fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, id ASC")
    private List<MembershipPlanFeature> features = new ArrayList<>();
}
