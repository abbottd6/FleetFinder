package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.RsvpMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RsvpMasterRepository extends JpaRepository<RsvpMaster, Long> {

    @Query(value = "SELECT * FROM rsvp_master WHERE listing_id = :listingId", nativeQuery = true)
    List<RsvpMaster> findAllByListingId(@Param("listingId") Long listingId);
}
