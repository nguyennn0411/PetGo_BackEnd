package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "provider_business_hours")
@Getter
@Setter
public class ProviderBusinessHour extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @Column(nullable = false)
    private Integer weekday;

    @Column(name = "opens_at")
    private LocalTime opensAt;

    @Column(name = "closes_at")
    private LocalTime closesAt;

    @Column(name = "break_starts_at")
    private LocalTime breakStartsAt;

    @Column(name = "break_ends_at")
    private LocalTime breakEndsAt;

    @Column(name = "is_closed", nullable = false)
    private Boolean closed;
}
