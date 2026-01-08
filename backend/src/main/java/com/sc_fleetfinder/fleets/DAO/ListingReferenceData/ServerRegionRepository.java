package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRegionRepository extends JpaRepository<ServerRegion, Integer> {
}
