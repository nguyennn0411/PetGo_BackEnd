package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "registration_applications",
        uniqueConstraints = @UniqueConstraint(name = "uk_registration_user_type", columnNames = {"user_id", "type"})
)
@Getter
@Setter
public class RegistrationApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RegistrationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RegistrationStatus status;

    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;

    @Column(name = "business_phone", nullable = false, length = 50)
    private String businessPhone;

    @Column(name = "business_email", nullable = false, length = 255)
    private String businessEmail;

    @Column(name = "business_address", nullable = false, length = 500)
    private String businessAddress;

    @Column(name = "tax_code", length = 100)
    private String taxCode;

    @Column(name = "representative_name", nullable = false, length = 255)
    private String representativeName;

    @Column(name = "representative_phone", nullable = false, length = 50)
    private String representativePhone;

    @Column(name = "representative_email", nullable = false, length = 255)
    private String representativeEmail;

    @Column(name = "service_category_ids", length = 500)
    private String serviceCategoryIds;

    @Column(name = "location_image_urls", columnDefinition = "TEXT")
    private String locationImageUrls;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "additional_information", columnDefinition = "TEXT")
    private String additionalInformation;

    @Column(name = "admin_message", columnDefinition = "TEXT")
    private String adminMessage;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
}