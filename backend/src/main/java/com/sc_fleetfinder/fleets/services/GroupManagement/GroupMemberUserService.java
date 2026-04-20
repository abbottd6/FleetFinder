package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface GroupMemberUserService extends GroupMemberService {

    Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user);

    Page<GroupListingResponseDto> getMyInviteAuthorizedMemberships(Users user);

    GroupInviteRequestOrResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto);

    GroupMembershipResponseDto acceptGroupInviteOffer(Users newMember, GroupInviteRequestOrResponseDto dto);

    void userLeaveGroup(Users user, Long groupId);

}
