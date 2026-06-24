package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "areas")
@Getter
@Setter
public class Area extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "ward_code", length = 50)
    private String wardCode;

    @Column(name = "district_code", length = 50)
    private String districtCode;

    @Column(name = "province_code", length = 50)
    private String provinceCode;

    @Column(name = "pickup_latitude", precision = 10, scale = 7)
    private BigDecimal pickupLatitude;

    @Column(name = "pickup_longitude", precision = 10, scale = 7)
    private BigDecimal pickupLongitude;

    @Column(name = "pickup_address", length = 500)
    private String pickupAddress;

    @Column(name = "pickup_phone", length = 20)
    private String pickupPhone;

    @Column(name = "pickup_instructions", columnDefinition = "TEXT")
    private String pickupInstructions;

    @Column(name = "short_slots", nullable = false)
    private Integer shortSlots = 10;

    @Column(name = "long_slots", nullable = false)
    private Integer longSlots = 3;
}
