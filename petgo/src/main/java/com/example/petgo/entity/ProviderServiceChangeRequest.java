package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_service_change_requests")
@Getter
@Setter
public class ProviderServiceChangeRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_service_id")
    private ProviderService providerService;

    @Column(name = "request_type", nullable = false, length = 20)
    private String requestType;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "category_ids", length = 500)
    private String categoryIds;

    @Column(name = "service_name", length = 150)
    private String serviceName;

    @Column(name = "photo_urls", columnDefinition = "TEXT")
    private String photoUrls;

    @Column(name = "price_amount", precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "price_unit", length = 40)
    private String priceUnit;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "admin_message", columnDefinition = "TEXT")
    private String adminMessage;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
}