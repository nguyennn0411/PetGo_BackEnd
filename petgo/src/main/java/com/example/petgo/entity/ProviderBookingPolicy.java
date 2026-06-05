package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "provider_booking_policies")
@Getter
@Setter
public class ProviderBookingPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @Column(name = "cancel_window_hours", nullable = false)
    private Integer cancelWindowHours;

    @Column(name = "cancel_fee_type", nullable = false, length = 20)
    private String cancelFeeType;

    @Column(name = "cancel_fee_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal cancelFeeAmount;

    @Column(name = "cancel_fee_applies_after_hours")
    private Integer cancelFeeAppliesAfterHours;

    @Column(name = "allow_user_reschedule", nullable = false)
    private Boolean allowUserReschedule;

    @Column(name = "reschedule_window_hours", nullable = false)
    private Integer rescheduleWindowHours;

    @Column(name = "max_reschedules_per_booking", nullable = false)
    private Integer maxReschedulesPerBooking;
}