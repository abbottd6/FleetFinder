package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.Users;

import java.util.Optional;

public interface GroupMemberService {

    Boolean verifyUserIsAuthorizedMember(Users user, Long listingId);

    Optional<CrewPosition> findGroupMemberCrewPosition(GroupMember member);

    boolean hasGroupManagementPrivileges(InGroupRank rank);

    Boolean getNewMemberHasExternalNotes(Users newMember);

    void createOwnerMember(Users user, GroupListing listing);

    GroupInviteRequestOrResponseDto declineGroupInviteOfferOrRequest(Users actingUser, GroupInviteRequestOrResponseDto dto);

    GroupInviteRequestOrResponseDto rescindGroupInviteOfferOrRequest(Users actingUser, GroupInviteRequestOrResponseDto dto);
}
