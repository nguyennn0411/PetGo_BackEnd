package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "membership_plan_features")
@Getter
@Setter
public class MembershipPlanFeature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_plan_id", nullable = false)
    private MembershipPlan membershipPlan;

    @Column(name = "feature_text", nullable = false, length = 255)
    private String featureText;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
