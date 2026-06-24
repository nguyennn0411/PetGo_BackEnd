package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provider_photos")
@Getter
@Setter
public class ProviderPhoto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    @Column(name = "media_type", nullable = false, length = 20)
    private String mediaType;

    @Column(name = "is_primary", nullable = false)
    private Boolean primary;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
