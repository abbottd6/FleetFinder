package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByKeycloakId(String kcId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsernameIgnoreCase(String username);
    Optional<Users> findByDiscordId(String discordId);
}
