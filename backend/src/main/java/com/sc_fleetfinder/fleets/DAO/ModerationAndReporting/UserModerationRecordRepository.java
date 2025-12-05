package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.UserModerationRecord;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserModerationRecordRepository extends JpaRepository<UserModerationRecord, Long> {
    Optional<UserModerationRecord> findByUser(Users user);
}
