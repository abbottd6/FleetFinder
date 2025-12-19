package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingBookmark;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface ListingBookmarkRepository extends JpaRepository<ListingBookmark, Long> {

    @Query("SELECT lb.group FROM ListingBookmark lb WHERE lb.user = :user")
    Set<GroupListing> findByUser(@Param("user") Users user);

    @Query("SELECT lb.group FROM ListingBookmark lb WHERE lb.user = :user")
    Page<GroupListing> pageByUser(@Param("user") Users user, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ListingBookmark lb WHERE lb.user = :user AND lb.group.groupId IN (:groupIds)")
    Integer deleteMultiple(@Param("user") Users user, @Param("groupIds") Set<Long> groupIds);

    Set<ListingBookmark> findByGroup(GroupListing groupListing);

    Optional<ListingBookmark> findByUserAndGroup(Users user, GroupListing group);

    @Query("SELECT lb FROM ListingBookmark lb WHERE lb.group.groupId = :groupId")
    Optional<ListingBookmark> findByUserAndGroupId(@Param("user") Users user, @Param("groupId") Long groupId);
}
