package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerInviteResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface GroupMemberManagementService {

    Boolean verifyUserIsAuthorizedMember(Users user, Long listingId);

    Page<GroupManagerMemberResponseDto> getActiveRosterGroupMembers(Users user, Long listingId);

    Page<GroupManagerMemberResponseDto> getWaitlistMembers(Users user, Long listingId);

    Page<GroupManagerInviteResponseDto> getGroupInvitesPage(Users user, Long listingId);

    GroupManagerMemberResponseDto acceptGroupInviteRequest(Users user, Long listingId, Long inviteId);

    void declineGroupInviteRequest(Users user, Long listingId, Long inviteId);

    GroupManagerInviteResponseDto sendGroupInviteOffer(Users sender, Long listingId, Long recipientId);
}
