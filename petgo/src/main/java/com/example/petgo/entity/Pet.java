package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Getter
@Setter
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_code", nullable = false, length = 32)
    private String petCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 20)
    private String species;

    @Column(length = 120)
    private String breed;

    @Column(length = 20)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "age_label", length = 50)
    private String ageLabel;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(length = 100)
    private String color;

    @Column(length = 20)
    private String size;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "health_notes", columnDefinition = "TEXT")
    private String healthNotes;

    @Column(name = "allergy_notes", columnDefinition = "TEXT")
    private String allergyNotes;

    @Column(name = "behavior_notes", columnDefinition = "TEXT")
    private String behaviorNotes;

    @Column(name = "vaccination_notes", columnDefinition = "TEXT")
    private String vaccinationNotes;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
