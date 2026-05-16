package com.example.petgo.repository;

import com.example.petgo.entity.MembershipSubscription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MembershipSubscriptionRepository extends JpaRepository<MembershipSubscription, Long> {

    @EntityGraph(attributePaths = {"membershipPlan", "membershipPlan.features", "user"})
    Optional<MembershipSubscription> findTopByUser_IdAndStatusInOrderByCreatedAtDescIdDesc(Long userId, Collection<String> statuses);

    @EntityGraph(attributePaths = {"membershipPlan", "membershipPlan.features", "user"})
    Optional<MembershipSubscription> findTopByUser_IdOrderByCreatedAtDescIdDesc(Long userId);

    @EntityGraph(attributePaths = {"membershipPlan", "membershipPlan.features", "user"})
    Optional<MembershipSubscription> findByIdAndUser_Id(Long id, Long userId);

    @EntityGraph(attributePaths = {"membershipPlan", "membershipPlan.features", "user"})
    List<MembershipSubscription> findByUser_IdOrderByCreatedAtDescIdDesc(Long userId);

    long countByUser_Id(Long userId);

    long countByUser_IdAndStatusIn(Long userId, Collection<String> statuses);
}
