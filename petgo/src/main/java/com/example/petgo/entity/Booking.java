package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_code", nullable = false, length = 32)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id", nullable = false)
    private User customerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_service_id", nullable = false)
    private ProviderService providerService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_slot_id")
    private ProviderAvailabilitySlot availabilitySlot;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "cancellation_reason_code", length = 50)
    private String cancellationReasonCode;

    @Column(name = "customer_note", columnDefinition = "TEXT")
    private String customerNote;

    @Column(name = "internal_note", columnDefinition = "TEXT")
    private String internalNote;

    @Column(name = "reschedule_count", nullable = false)
    private Integer rescheduleCount;

    @Column(name = "provider_name_snapshot", nullable = false, length = 180)
    private String providerNameSnapshot;

    @Column(name = "provider_phone_snapshot", length = 30)
    private String providerPhoneSnapshot;

    @Column(name = "provider_address_snapshot", length = 255)
    private String providerAddressSnapshot;

    @Column(name = "service_name_snapshot", nullable = false, length = 150)
    private String serviceNameSnapshot;

    @Column(name = "service_description_snapshot", length = 255)
    private String serviceDescriptionSnapshot;

    @Column(name = "service_duration_minutes_snapshot", nullable = false)
    private Integer serviceDurationMinutesSnapshot;

    @Column(name = "pet_name_snapshot", nullable = false, length = 120)
    private String petNameSnapshot;

    @Column(name = "pet_breed_snapshot", length = 120)
    private String petBreedSnapshot;

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "membership_discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal membershipDiscountAmount;

    @Column(name = "promo_discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal promoDiscountAmount;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;
}
