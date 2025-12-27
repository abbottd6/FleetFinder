package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PvpStatusRepository extends JpaRepository<PvpStatus, Integer> {
}
