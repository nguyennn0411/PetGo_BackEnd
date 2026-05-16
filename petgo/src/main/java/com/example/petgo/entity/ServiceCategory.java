package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_categories")
@Getter
@Setter
public class ServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ServiceCategory parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name ASC, id ASC")
    private List<ServiceCategory> children = new ArrayList<>();

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
