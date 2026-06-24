package com.example.petgo.repository;

import com.example.petgo.entity.UserFavoriteService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFavoriteServiceRepository extends JpaRepository<UserFavoriteService, Long> {

    List<UserFavoriteService> findByUserId(Long userId);

    Optional<UserFavoriteService> findByUserIdAndServiceId(Long userId, Long serviceId);

    boolean existsByUserIdAndServiceId(Long userId, Long serviceId);

    void deleteByUserIdAndServiceId(Long userId, Long serviceId);
}
