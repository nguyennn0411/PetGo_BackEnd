package com.example.petgo.repository;

import com.example.petgo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    Optional<User> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);

    Optional<User> findByUserCodeAndDeletedAtIsNull(String userCode);

    List<User> findByIdInAndDeletedAtIsNull(Collection<Long> ids);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);
}
