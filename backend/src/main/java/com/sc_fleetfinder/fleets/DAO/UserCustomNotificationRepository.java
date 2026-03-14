package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.UserCustomNotification;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCustomNotificationRepository extends JpaRepository<UserCustomNotification, Long> {

    List<UserCustomNotification> findByUser(Users user);

    @Modifying
    @Query("DELETE FROM UserCustomNotification ucn WHERE ucn.user.userId = :userId AND ucn.customNoteId = :noteId")
    int deleteByUserIdAndNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);
}
