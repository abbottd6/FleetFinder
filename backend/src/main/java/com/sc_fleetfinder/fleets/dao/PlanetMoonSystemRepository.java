package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetMoonSystemRepository extends JpaRepository<PlanetMoonSystem, Integer> {
}
