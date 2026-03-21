package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.UserCustomNotification;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCustomNotificationRepository extends JpaRepository<UserCustomNotification, Long> {

    Page<UserCustomNotification> findByUser(Users user, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserCustomNotification ucn WHERE ucn.user.userId = :userId AND ucn.customNoteId = :noteId")
    int deleteByUserIdAndNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);

    Integer countByUser(Users user);

    @Query("SELECT COUNT(ucn) FROM UserCustomNotification ucn WHERE ucn.user.userId = :userId AND ucn.enabled = true")
    Integer countEnabledByUser(@Param("userId") Long userId);

    Optional<UserCustomNotification> findByUserAndCustomNoteId(Users user, Long customNoteId);
}
