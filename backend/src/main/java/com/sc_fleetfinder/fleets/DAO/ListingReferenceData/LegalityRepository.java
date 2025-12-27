package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalityRepository extends JpaRepository<Legality, Integer> {
}
