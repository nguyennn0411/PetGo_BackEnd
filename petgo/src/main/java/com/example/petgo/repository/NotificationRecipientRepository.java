package com.example.petgo.repository;

import com.example.petgo.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {

    @Query("""
            select nr
            from NotificationRecipient nr
            join fetch nr.notification notification
            left join fetch notification.createdBy
            where nr.recipient.id = :userId
              and (notification.expiresAt is null or notification.expiresAt > :now)
            order by nr.deliveredAt desc, nr.id desc
            """)
    List<NotificationRecipient> findActiveByRecipientId(@Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query("""
            select nr
            from NotificationRecipient nr
            join fetch nr.notification notification
            left join fetch notification.createdBy
            where nr.recipient.id = :userId
              and nr.readAt is null
              and (notification.expiresAt is null or notification.expiresAt > :now)
            order by nr.deliveredAt desc, nr.id desc
            """)
    List<NotificationRecipient> findActiveUnreadByRecipientId(@Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query("""
            select count(nr)
            from NotificationRecipient nr
            join nr.notification notification
            where nr.recipient.id = :userId
              and (notification.expiresAt is null or notification.expiresAt > :now)
            """)
    long countActiveByRecipientId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
            select count(nr)
            from NotificationRecipient nr
            join nr.notification notification
            where nr.recipient.id = :userId
              and nr.readAt is null
              and (notification.expiresAt is null or notification.expiresAt > :now)
            """)
    long countActiveUnreadByRecipientId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    Optional<NotificationRecipient> findByNotification_IdAndRecipient_Id(Long notificationId, Long recipientId);

    long countByNotification_Id(Long notificationId);

    long countByNotification_IdAndReadAtIsNotNull(Long notificationId);
}