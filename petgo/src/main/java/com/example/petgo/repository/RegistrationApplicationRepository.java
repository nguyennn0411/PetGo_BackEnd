package com.example.petgo.repository;

import com.example.petgo.entity.RegistrationApplication;
import com.example.petgo.entity.RegistrationStatus;
import com.example.petgo.entity.RegistrationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationApplicationRepository extends JpaRepository<RegistrationApplication, Long> {

    @EntityGraph(attributePaths = { "user", "reviewer" })
    Optional<RegistrationApplication> findByUser_IdAndType(Long userId, RegistrationType type);

    @EntityGraph(attributePaths = { "user", "reviewer" })
    @Query("select r from RegistrationApplication r where r.id = :id")
    Optional<RegistrationApplication> findWithUserById(@Param("id") Long id);

    @EntityGraph(attributePaths = { "user", "reviewer" })
    List<RegistrationApplication> findByTypeOrderBySubmittedAtDescCreatedAtDesc(RegistrationType type);

    @EntityGraph(attributePaths = { "user", "reviewer" })
    List<RegistrationApplication> findByTypeAndStatusOrderBySubmittedAtDescCreatedAtDesc(RegistrationType type,
            RegistrationStatus status);
}