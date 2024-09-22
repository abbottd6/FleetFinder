package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlanetarySystemRepository extends JpaRepository<PlanetarySystem, Integer> {
}
