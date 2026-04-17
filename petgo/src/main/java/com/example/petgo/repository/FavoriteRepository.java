package com.example.petgo.repository;

import com.example.petgo.entity.Favorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @EntityGraph(attributePaths = {"user", "provider"})
    List<Favorite> findByUser_IdOrderByCreatedAtDescIdDesc(Long userId);

    List<Favorite> findByUser_IdAndProvider_StatusAndProvider_DeletedAtIsNullOrderByCreatedAtDescIdDesc(Long userId, String status);

    boolean existsByUser_IdAndProvider_Id(Long userId, Long providerId);

    Optional<Favorite> findByUser_IdAndProvider_Id(Long userId, Long providerId);

    void deleteByUser_IdAndProvider_Id(Long userId, Long providerId);
}
