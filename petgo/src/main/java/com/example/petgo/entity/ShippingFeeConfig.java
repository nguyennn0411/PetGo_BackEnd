package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_fee_configs")
@Getter
@Setter
public class ShippingFeeConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "from_km", nullable = false, precision = 8, scale = 2)
    private BigDecimal fromKm;

    @Column(name = "to_km", precision = 8, scale = 2)
    private BigDecimal toKm;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal fee;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
}
