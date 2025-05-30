package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByKeycloakId(String kcId);
}
