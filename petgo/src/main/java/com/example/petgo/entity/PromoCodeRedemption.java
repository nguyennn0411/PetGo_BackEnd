package com.example.petgo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_code_redemptions")
@Getter
@Setter
public class PromoCodeRedemption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id", nullable = false)
    private PromoCode promoCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_subscription_id")
    private MembershipSubscription membershipSubscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_booking_id")
    private ShippingBooking shippingBooking;

    @Column(name = "promo_code_snapshot", nullable = false, length = 50)
    private String promoCodeSnapshot;

    @Column(name = "owner_type", nullable = false, length = 20)
    private String ownerType;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;
}