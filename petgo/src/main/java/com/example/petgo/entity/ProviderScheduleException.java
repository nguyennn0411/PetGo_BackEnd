package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "provider_schedule_exceptions")
@Getter
@Setter
public class ProviderScheduleException extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @Column(name = "local_date", nullable = false)
    private LocalDate localDate;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "starts_at_local")
    private LocalTime startsAtLocal;

    @Column(name = "ends_at_local")
    private LocalTime endsAtLocal;

    @Column(name = "max_concurrent_override")
    private Integer maxConcurrentOverride;

    @Column(columnDefinition = "TEXT")
    private String reason;
}