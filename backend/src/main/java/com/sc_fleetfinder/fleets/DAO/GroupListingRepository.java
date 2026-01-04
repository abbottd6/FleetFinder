package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface GroupListingRepository extends JpaRepository<GroupListing, Long>, JpaSpecificationExecutor<GroupListing> {

    @Query("select gl.groupId from GroupListing gl where gl.groupId in :ids")
    Set<Long> findAllIds(@Param("ids") Set<Long> ids);
}
