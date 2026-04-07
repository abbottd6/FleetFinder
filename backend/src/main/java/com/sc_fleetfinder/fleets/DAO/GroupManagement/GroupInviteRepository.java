package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {

    @Query("""
            SELECT i FROM GroupInvite i
            WHERE i.sender = :sender
                AND i.recipient = :recipient
                AND i.groupListing.groupId = :listingId
                AND i.inviteDirection = :dir
            """)
    Optional<GroupInvite> findBySenderListingAndDirection(
            @Param("sender") Users sender,
            @Param("recipient") Users recip,
            @Param("listingId") Long listingId,
            @Param("dir") InviteDirection dir);

    @Modifying
    @Query("""
            DELETE FROM GroupInvite gi
            WHERE gi.groupListing.groupId = :listingId
                        AND ((gi.sender.userId = :userId AND gi.inviteDirection = "REQUEST")
                        OR (gi.recipient.userId = :userId AND gi.inviteDirection = "OFFER"))
            """)
    void deleteByUserAndGroupListing(@Param("userId") Long userId, @Param("listingId") Long listingId);
}
