package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupStatusRepository extends JpaRepository<GroupStatus, Integer> {
}
