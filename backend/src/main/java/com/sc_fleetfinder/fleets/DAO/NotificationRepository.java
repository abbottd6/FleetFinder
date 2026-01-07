package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT n FROM Notification n
            WHERE n.user.userId=:userId
            ORDER BY n.createdAt DESC
            """)
    Page<Notification> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    Integer deleteAllByUser_userId(Long userId);

    @Query("""
            SELECT COUNT(n)
            FROM Notification n
            WHERE n.user.userId = :userId AND n.readAt IS NULL
            """)
    Integer countUnreadByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Notification n
            SET n.readAt = CURRENT_TIMESTAMP
            WHERE n.user.userId = :userId
                AND n.notificationId IN :readIds
                AND n.readAt IS NULL
            """)
    int markAsRead(@Param("userId") Long userId,
                                  @Param("readIds")Collection<Long> readIds);
}
