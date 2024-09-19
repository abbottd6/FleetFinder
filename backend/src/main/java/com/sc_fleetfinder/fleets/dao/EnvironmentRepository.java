package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<GameEnvironment, Integer> {
}
