package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerInviteResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.UserMonikerSummary;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface GroupMemberManagementService extends GroupMemberService {

    Page<GroupManagerMemberResponseDto> getActiveRosterGroupMembers(Users user, Long listingId);

    Page<GroupManagerMemberResponseDto> getWaitlistMembers(Users user, Long listingId);

    Page<GroupManagerInviteResponseDto> getGroupInvitesPage(Users user, Long listingId);

    GroupManagerMemberResponseDto provisionNewGroupMemberFromJoinRequest(Users user, GroupManagerInviteResponseDto dto);

    GroupManagerInviteResponseDto declineGroupInviteRequest(Users actingUser, Long inviteId);

    UserMonikerSummary blockJoinRequestsFromRequestingUserForThisGroup(Users actingUser, Long inviteId);

    GroupManagerInviteResponseDto mirrorJoinRequestForActiveRosterToWaitlistInvite(Users actingUser, Long inviteId);

    GroupManagerInviteResponseDto sendGroupInviteOffer(Users sender, SendGroupInviteOfferDto dto);

    GroupManagerInviteResponseDto rescindGroupInviteOffer(Users manager, Long inviteId);
}
