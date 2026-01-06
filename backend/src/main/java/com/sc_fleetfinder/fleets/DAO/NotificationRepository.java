package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT n FROM Notification n
            WHERE n.user.userId=:userId
            ORDER BY n.createdAt DESC
            """)
    Page<Notification> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    Integer deleteAllByUser_userId(Long userId);
}
