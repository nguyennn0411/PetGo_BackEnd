    package com.example.petgo.entity;

    import com.example.petgo.entity.enums.Gender;
    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.Set;

    @Entity
    @Table(name = "users")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "full_name", nullable = false, length = 150)
        private String fullName;

        @Column(nullable = false, unique = true, length = 150)
        private String email;

        @Column(name = "password_hash", nullable = false, length = 255)
        private String passwordHash;

        @Column(nullable = false, unique = true, length = 30)
        private String phone;

        @Column(name = "avatar_url", length = 500)
        private String avatarUrl;

        @Column(name = "date_of_birth")
        private LocalDate dateOfBirth;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Gender gender;

        @Column(length = 255)
        private String street;

        @Column(length = 120)
        private String ward;

        @Column(length = 120)
        private String district;

        @Column(length = 120)
        private String city;

        @Column(length = 120)
        private String country;

        @Column(precision = 10, scale = 7)
        private BigDecimal latitude;

        @Column(precision = 10, scale = 7)
        private BigDecimal longitude;

        @Column(name = "is_active", nullable = false)
        private Boolean isActive;

        @Column(name = "email_verified_at")
        private LocalDateTime emailVerifiedAt;

        @Column(name = "phone_verified_at")
        private LocalDateTime phoneVerifiedAt;

        @Column(name = "last_login_at")
        private LocalDateTime lastLoginAt;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @Column(name = "deleted_at")
        private LocalDateTime deletedAt;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )
        private Set<Role> roles;
    }