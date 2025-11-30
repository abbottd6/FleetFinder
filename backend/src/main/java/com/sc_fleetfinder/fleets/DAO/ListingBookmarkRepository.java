package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface ListingBookmarkRepository extends JpaRepository<ListingBookmark, Long> {

    Set<ListingBookmark> findByUser(Users user);
    Set<ListingBookmark> findByGroup(GroupListing groupListing);
    Optional<ListingBookmark> findByUserAndGroup(Users user, GroupListing group);
}
