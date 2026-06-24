package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "booking_reschedules")
@Getter
@Setter
public class BookingReschedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id", nullable = false)
    private User requestedByUser;

    @Column(name = "old_appointment_date", nullable = false)
    private LocalDate oldAppointmentDate;

    @Column(name = "old_start_time", nullable = false)
    private LocalTime oldStartTime;

    @Column(name = "old_end_time", nullable = false)
    private LocalTime oldEndTime;

    @Column(name = "new_appointment_date", nullable = false)
    private LocalDate newAppointmentDate;

    @Column(name = "new_start_time", nullable = false)
    private LocalTime newStartTime;

    @Column(name = "new_end_time", nullable = false)
    private LocalTime newEndTime;

    @Column(name = "fee_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal feeAmount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 255)
    private String note;
}
