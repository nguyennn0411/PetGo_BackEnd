package com.example.petgo.repository;

import com.example.petgo.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    boolean existsByConversation_IdAndUser_IdAndLeftAtIsNull(Long conversationId, Long userId);

    Optional<ChatParticipant> findByConversation_IdAndUser_IdAndLeftAtIsNull(Long conversationId, Long userId);

    List<ChatParticipant> findByConversation_IdAndLeftAtIsNullOrderByIdAsc(Long conversationId);
}