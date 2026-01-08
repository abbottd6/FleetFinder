package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetMoonSystemRepository extends JpaRepository<PlanetMoonSystem, Integer> {
}
