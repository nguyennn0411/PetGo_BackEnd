package com.example.petgo.repository;

import com.example.petgo.entity.Conversation;
import com.example.petgo.entity.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN FETCH c.user WHERE c.deletedAt IS NULL AND c.user.id = :userId ORDER BY c.updatedAt DESC")
    List<Conversation> findByUserIdAndDeletedAtIsNullOrderByUpdatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c JOIN FETCH c.user WHERE c.deletedAt IS NULL AND c.type = :type ORDER BY c.updatedAt DESC")
    List<Conversation> findByTypeAndDeletedAtIsNullOrderByUpdatedAtDesc(@Param("type") ConversationType type);

    @Query("SELECT c FROM Conversation c JOIN FETCH c.user WHERE c.deletedAt IS NULL ORDER BY c.updatedAt DESC")
    List<Conversation> findAllByDeletedAtIsNullOrderByUpdatedAtDesc();

    @Query("SELECT c FROM Conversation c JOIN FETCH c.user WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Conversation> findActiveById(@Param("id") Long id);

    @Query("SELECT c FROM Conversation c JOIN FETCH c.user WHERE c.id = :id AND c.deletedAt IS NULL AND c.user.id = :userId")
    Optional<Conversation> findActiveOwnedById(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c JOIN FETCH c.user WHERE c.deletedAt IS NULL AND c.user.id = :userId AND c.type = :type AND c.status IN ('OPEN', 'PROCESSING') ORDER BY c.updatedAt DESC")
    List<Conversation> findActiveByUserIdAndType(@Param("userId") Long userId, @Param("type") ConversationType type);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.deletedAt IS NULL AND c.status = :status")
    long countByStatusAndDeletedAtIsNull(@Param("status") String status);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.deletedAt IS NULL AND c.type = :type AND c.status = :status")
    long countByTypeAndStatusAndDeletedAtIsNull(@Param("type") ConversationType type, @Param("status") String status);
}
