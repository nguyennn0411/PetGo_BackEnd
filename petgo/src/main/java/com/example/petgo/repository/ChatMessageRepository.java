package com.example.petgo.repository;

import com.example.petgo.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversation_IdAndDeletedAtIsNullOrderByIdDesc(Long conversationId, Pageable pageable);

    @Query("""
            select m
            from ChatMessage m
            where m.conversation.id = :conversationId
              and m.messageType = 'IMAGE'
              and m.deletedAt is null
            order by m.id desc
            """)
    List<ChatMessage> findActiveImagesDesc(@Param("conversationId") Long conversationId);
}