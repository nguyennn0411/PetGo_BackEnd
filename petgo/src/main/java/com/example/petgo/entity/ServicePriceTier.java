package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "service_price_tiers")
@Getter
@Setter
public class ServicePriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private CatalogService service;

    @Column(nullable = false, length = 10)
    private String species;

    @Column(name = "weight_from", precision = 8, scale = 2, nullable = false)
    private BigDecimal weightFrom;

    @Column(name = "weight_to", precision = 8, scale = 2, nullable = false)
    private BigDecimal weightTo;

    @Column(name = "price_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal priceAmount;
}
