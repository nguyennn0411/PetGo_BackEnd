package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "provider_availability_slots")
@Getter
@Setter
public class ProviderAvailabilitySlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_service_id")
    private ProviderService providerService;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_status", nullable = false, length = 20)
    private String slotStatus;

    @Column(name = "capacity_total", nullable = false)
    private Integer capacityTotal;

    @Column(name = "capacity_booked", nullable = false)
    private Integer capacityBooked;

    @Column(length = 255)
    private String note;
}
