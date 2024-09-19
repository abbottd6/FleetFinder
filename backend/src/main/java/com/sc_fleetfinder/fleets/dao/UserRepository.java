package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
