package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InGroupRankRepository extends JpaRepository<InGroupRank, Long> {

    @Query("""
            SELECT r FROM InGroupRank r
                WHERE r.groupListing IS NULL
                AND r.rankSubgroupScope IS NULL
           """)
    List<InGroupRank> findAllGenericRanks();

    @Query(value = """
            SELECT p.privilegeType FROM GroupRankAssignedPrivilege p
            JOIN GroupMember m ON m.groupListing.groupId = :listingId
                        AND m.user.userId = :userId
            JOIN InGroupRank r ON r.groupListing.groupId = :listingId
                        AND r.rankId = m.memberRank.rankId
            WHERE r.rankId = m.memberRank.rankId
            """)
    Set<RankPrivilegeOptions> findPrivilegesByUserIdAndListingId(
            @Param("userId") Long userId, @Param("listingId") Long listingId);
}
