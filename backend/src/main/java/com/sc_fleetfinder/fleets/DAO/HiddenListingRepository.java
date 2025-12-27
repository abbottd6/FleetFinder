package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.HiddenListing;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface HiddenListingRepository extends JpaRepository<HiddenListing, Integer> {
    Set<HiddenListing> findByUser(Users user);
    Optional<HiddenListing> findByUserAndListing(Users user, GroupListing listing);
    Optional<HiddenListing> findTopByUserOrderByHiddenAtDesc(Users user);
    void deleteAllByUser(Users user);
}
