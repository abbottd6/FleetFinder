package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = """
           SELECT cp From CrewPosition cp
           WHERE cp.assignedMemberUserId = :userId
                 AND cp.groupListing.groupId = :listingId
           """)
    Optional<CrewPosition> findPositionByAssignedMemberUserIdAndListingId(@Param("userId") Long userId,
                                                                          @Param("listingId") Long listingId);

    @Query("""
           SELECT cp FROM CrewPosition cp
           WHERE cp.rootSubgroupId IN :rootIds
                AND cp.groupListing.groupId = :groupId
                AND cp.deletedAt IS NULL
           ORDER BY cp.sortOrder
           """)
    List<CrewPosition> findAllPositionsForSubgroupTrees_ExcludeDeleted(@Param("rootIds") List<Long> rootIds,
                                                                       @Param("groupId") Long groupId);

    @Query("""
           SELECT cp FROM CrewPosition cp
           WHERE cp.groupListing.groupId = :groupId
           """)
    List<CrewPosition> findAllPositionsByGroupId_IncludeDeleted(@Param("groupId") Long groupId);

    @Modifying
    @Query(value = """
            UPDATE mgmt_crew_position cp
            SET cp.deleted_at = NOW(),
                cp.assigned_member_id = NULL
            WHERE cp.subgroup_id = :subId
            """, nativeQuery = true)
    void softDeleteAllBySubgroup(@Param("subId") Long subgroupId);
}
