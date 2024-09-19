package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetarySystemRepository extends JpaRepository<PlanetarySystem, Integer> {
}
