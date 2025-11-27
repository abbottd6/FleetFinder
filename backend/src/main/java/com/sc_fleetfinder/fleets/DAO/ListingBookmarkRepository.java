package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface ListingBookmarkRepository extends JpaRepository<ListingBookmark, Long> {

    Optional<ListingBookmark> findByUserId(long userId);
    Optional<ListingBookmark> findByListingId(long listingId);
}
