package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupListingRepository extends JpaRepository<GroupListing, Integer> {
}
