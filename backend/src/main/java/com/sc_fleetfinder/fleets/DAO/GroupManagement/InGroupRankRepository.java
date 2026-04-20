package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            SELECT p.privilegeType.privilegeType FROM GroupRankAssignedPrivilege p
            JOIN InGroupRank r ON r.rankId = p.assignedToRank.rankId
            JOIN GroupMember m ON m.memberRank.rankId = r.rankId
                        AND m.user.userId = :userId
                        AND m.groupListing.groupId = :listingId
            """)
    Set<RankPrivilegeOptions> findPrivilegesByUserIdAndListingId(
            @Param("userId") Long userId, @Param("listingId") Long listingId);

    @Query(value = """
           SELECT listing FROM GroupListing listing
           JOIN GroupMember m ON m.groupListing.groupId = listing.groupId
           JOIN GroupRankAssignedPrivilege p ON p.assignedToRank.rankId = m.memberRank.rankId
           WHERE m.user.userId = :userId
                      AND p.privilegeType.privilegeType = 'INVITE'
           """)
    Page<GroupListing> findMyInviteAuthorizedGroups(@Param("userId") Long userId, Pageable pageable);
}
