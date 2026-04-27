package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    Page<GroupMember> findAllByUserOrderByCreatedAtDesc(Users user, Pageable pageable);

    @Query(value = """
            SELECT * FROM group_member m
            JOIN group_listing l ON m.listing_id = l.id_group
            WHERE m.user_id = :userId
            ORDER BY
                CASE
                    WHEN l.event_schedule IS NOT NULL
                    THEN ABS(TIMESTAMPDIFF(SECOND, NOW(), l.event_schedule))
                    ELSE ABS(TIMESTAMPDIFF(SECOND, NOW(), l.creation_timestamp))
                END
            """,
            countQuery = "SELECT count(*) FROM group_member WHERE user_id = :userId",
            nativeQuery = true)
    Page<GroupMember> findAllByUserOrderByEventTimeProximity(@Param("userId") Long userId, Pageable pageable);

    Optional<GroupMember> findByUserAndGroupListing(Users user, GroupListing listing);

    Integer deleteByUserAndGroupListing(Users user, GroupListing listing);

    @Query("""
            SELECT m FROM GroupMember m
            WHERE m.groupListing.groupId = :listingId
                AND m.memberStatus = 'ACTIVE'
            """)
    Page<GroupMember> findActiveRosterMembersByGroup(@Param("listingId") Long listingId, Pageable pageable);

    @Query("""
            SELECT m FROM GroupMember m
            WHERE m.groupListing.groupId = :listingId
                AND m.memberStatus = 'WAITLIST'
            """)
    Page<GroupMember> findWaitlistMembersByGroup(@Param("listingId") Long listingId, Pageable pageable);
}
