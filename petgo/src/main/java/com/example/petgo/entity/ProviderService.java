package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "provider_services")
@Getter
@Setter
public class ProviderService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private CatalogService service;

    @Column(name = "custom_name", length = 150)
    private String customName;

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "price_unit", nullable = false, length = 20)
    private String priceUnit;

    @Column(name = "is_featured", nullable = false)
    private Boolean featured;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "capacity_per_slot", nullable = false)
    private Integer capacityPerSlot;

    @Column(name = "booking_buffer_minutes", nullable = false)
    private Integer bookingBufferMinutes;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "category_ids", length = 500)
    private String categoryIds;

    @Column(name = "photo_urls", columnDefinition = "TEXT")
    private String photoUrls;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus;
}
