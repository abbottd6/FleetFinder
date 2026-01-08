package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<GameEnvironment, Integer> {
}
