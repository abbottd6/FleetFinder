package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface GroupMemberUserService extends GroupMemberService {

    Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user, SortablePageRequestDto pageDto);

    Page<GroupInviteRequestOrResponseDto> getMyGroupInvites(Users user, GenericPageRequestDto dto);

    Page<GroupListingResponseDto> getMyInviteAuthorizedMemberships(Users user);

    GroupInviteRequestOrResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto);

    GroupInviteRequestOrResponseDto rescindGroupInviteRequest(Users sender, Long inviteId);

    GroupMembershipResponseDto acceptGroupInviteOffer(Users newMember, GroupInviteRequestOrResponseDto dto);

    GroupInviteRequestOrResponseDto declineGroupInviteOffer(Users invRecipient, Long inviteId);

    void userLeaveGroup(Users user, Long groupId);

}
