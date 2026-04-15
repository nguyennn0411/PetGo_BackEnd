package com.example.petgo.repository;

import com.example.petgo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Sửa từ findByPhone thành findByPhoneNumber
    Optional<User> findByPhoneNumber(String phoneNumber);

    Boolean existsByEmail(String email);

    // Sửa từ existsByPhone thành existsByPhoneNumber
    Boolean existsByPhoneNumber(String phoneNumber);
}