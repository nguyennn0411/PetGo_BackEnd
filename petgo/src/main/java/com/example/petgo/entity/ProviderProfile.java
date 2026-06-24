package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_profiles")
@Getter
@Setter
public class ProviderProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "provider_code", nullable = false, length = 32)
    private String providerCode;

    @Column(name = "business_name", nullable = false, length = 180)
    private String businessName;

    @Column(nullable = false, length = 190)
    private String slug;

    @Column(name = "provider_type", nullable = false, length = 30)
    private String providerType;

    @Column(length = 255)
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus;

    @Column(name = "is_featured", nullable = false)
    private Boolean featured;

    @Column(name = "is_hot", nullable = false)
    private Boolean hot;

    @Column(name = "accepts_instant_booking", nullable = false)
    private Boolean acceptsInstantBooking;

    @Column(name = "accepts_membership", nullable = false)
    private Boolean acceptsMembership;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews;

    @Column(name = "total_completed_bookings", nullable = false)
    private Integer totalCompletedBookings;

    @Column(name = "service_radius_km", precision = 6, scale = 2)
    private BigDecimal serviceRadiusKm;

    @Column(name = "cancellation_free_hours", nullable = false)
    private Integer cancellationFreeHours;

    @Column(name = "emergency_phone", length = 30)
    private String emergencyPhone;

    @Column(name = "primary_address_line1", length = 255)
    private String primaryAddressLine1;

    @Column(name = "primary_address_line2", length = 255)
    private String primaryAddressLine2;

    @Column(length = 120)
    private String ward;

    @Column(length = 120)
    private String district;

    @Column(length = 120)
    private String city;

    @Column(length = 120)
    private String province;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "price_from_amount", precision = 12, scale = 2)
    private BigDecimal priceFromAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
