package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface ListingBookmarkRepository extends JpaRepository<ListingBookmark, Long> {

    @Query("select lb.group from ListingBookmark lb where lb.user = :user")
    Set<GroupListing> findByUser(@Param("user") Users user);
    Set<ListingBookmark> findByGroup(GroupListing groupListing);
    Optional<ListingBookmark> findByUserAndGroup(Users user, GroupListing group);
}
