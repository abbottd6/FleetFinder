package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayStyleRepository extends JpaRepository<PlayStyle, Integer> {
}
