package com.example.petgo.repository;

import com.example.petgo.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    @Query("""
            select c
            from ChatConversation c
            where c.type = 'DIRECT_PROVIDER'
              and c.provider.id = :providerId
              and exists (
                select 1 from ChatParticipant p
                where p.conversation = c and p.user.id = :userId and p.leftAt is null
              )
            order by c.id desc
            """)
    Optional<ChatConversation> findDirectProviderConversation(@Param("userId") Long userId,
            @Param("providerId") Long providerId);

    @Query("""
            select c
            from ChatConversation c
            where c.type = 'SUPPORT_DIRECT'
              and exists (
                select 1 from ChatParticipant p
                where p.conversation = c and p.user.id = :userId and p.leftAt is null
              )
            order by c.id desc
            """)
    Optional<ChatConversation> findSupportConversation(@Param("userId") Long userId);

    @Query("""
            select c
            from ChatConversation c
            where c.type = 'BOOKING_GROUP'
              and c.booking.id = :bookingId
            order by c.id desc
            """)
    Optional<ChatConversation> findBookingConversation(@Param("bookingId") Long bookingId);

    @Query("""
            select distinct c
            from ChatConversation c
            join ChatParticipant p on p.conversation = c
            where p.user.id = :userId
              and p.leftAt is null
            order by c.lastMessageAt desc nulls last, c.id desc
            """)
    List<ChatConversation> findMyConversations(@Param("userId") Long userId);
}