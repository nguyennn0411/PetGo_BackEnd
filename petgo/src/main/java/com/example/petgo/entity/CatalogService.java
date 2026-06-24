package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Getter
@Setter
public class CatalogService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_code", nullable = false, length = 32)
    private String serviceCode;

    @ManyToMany
    @JoinTable(name = "service_category_mapping",
        joinColumns = @JoinColumn(name = "service_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<ServiceCategory> categories = new ArrayList<>();

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String slug;

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_duration_minutes", nullable = false)
    private Integer defaultDurationMinutes;

    @Column(name = "base_price_amount", precision = 12, scale = 2)
    private BigDecimal basePriceAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "price_unit", nullable = false, length = 20)
    private String priceUnit;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "booking_type", nullable = false, length = 20)
    private String bookingType = "SHORT";
}
