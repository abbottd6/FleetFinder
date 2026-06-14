package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewPositionRepository extends JpaRepository<CrewPosition, Long> {

    @Query(value = """
            SELECT cp FROM CrewPosition cp
            WHERE cp.assignedMember = :user
                AND cp.groupListing.groupId = :listingId
            """)
    Optional<CrewPosition> findMemberPositionByAssignedMemberIdAndListingId(
            @Param("user") GroupMember member,
            @Param("listingId") Long listingId);

    @Query("""
           SELECT cp FROM CrewPosition cp
           WHERE cp.rootSubgroupId IN :rootIds
                AND cp.groupListing.groupId = :groupId
           ORDER BY cp.sortOrder
           """)
    List<CrewPosition> findAllPositionsForSubgroupTrees(@Param("rootIds") List<Long> rootIds,
                                                        @Param("groupId") Long groupId);

    @Query("""
           SELECT cp FROM CrewPosition cp
           WHERE cp.groupListing.groupId = :groupId
           """)
    List<CrewPosition> findAllPositionsByGroupId(@Param("groupId") Long groupId);
}
