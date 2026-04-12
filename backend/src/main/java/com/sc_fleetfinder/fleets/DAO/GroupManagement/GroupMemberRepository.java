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

    Page<GroupMember> findAllByUser(Users user, Pageable pageable);

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
