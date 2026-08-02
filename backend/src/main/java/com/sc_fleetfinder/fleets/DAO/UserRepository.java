package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {
    Optional<Users> findByKeycloakId(String kcId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsernameIgnoreCase(String username);
    Optional<Users> findByDiscordId(String discordId);

    @Modifying
    @Query("UPDATE Users u SET u.lastAccess = :ts WHERE u.keycloakId = :kcId")
    void updateUserLastAccess(@Param("kcId") String kcId, @Param("ts") Instant ts);
}
