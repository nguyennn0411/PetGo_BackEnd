package com.example.petgo.repository;

import com.example.petgo.entity.WalletTransaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    @EntityGraph(attributePaths = { "user", "counterpartyUser", "reviewedByAdmin" })
    List<WalletTransaction> findByUserIdOrderByCreatedAtDescIdDesc(Long userId);

    @EntityGraph(attributePaths = { "user", "counterpartyUser", "reviewedByAdmin" })
    List<WalletTransaction> findByStatusOrderByCreatedAtAscIdAsc(String status);

    @EntityGraph(attributePaths = { "user", "counterpartyUser", "reviewedByAdmin" })
    List<WalletTransaction> findByTypeAndStatusOrderByCreatedAtDescIdDesc(String type, String status);

    @EntityGraph(attributePaths = { "user", "wallet", "counterpartyUser", "reviewedByAdmin" })
    Optional<WalletTransaction> findDetailedById(Long id);

    @EntityGraph(attributePaths = { "user", "wallet", "counterpartyUser", "reviewedByAdmin" })
    Optional<WalletTransaction> findByGatewayTransactionId(String gatewayTransactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = { "user", "wallet", "counterpartyUser", "reviewedByAdmin" })
    Optional<WalletTransaction> findFirstByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
            String gatewayTransactionId, String type, String status);
}