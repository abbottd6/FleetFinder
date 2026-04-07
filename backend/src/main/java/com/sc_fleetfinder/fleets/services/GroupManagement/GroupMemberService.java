package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface GroupMemberService {

    GroupMember createOwnerMember(Users user, GroupListing listing);

    Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user);

    GroupInviteRequestOrResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto);

    GroupInviteRequestOrResponseDto sendGroupInviteOffer(Users sender, SendGroupInviteOfferDto dto);

    GroupMembershipResponseDto acceptGroupInviteOffer(Users newMember, GroupInviteRequestOrResponseDto dto);

    GroupManagerMemberResponseDto acceptGroupInviteRequest(Users actingUser, GroupInviteRequestOrResponseDto dto);

    GroupInviteRequestOrResponseDto declineGroupInviteOfferOrRequest(Users actingUser, GroupInviteRequestOrResponseDto dto);

    GroupInviteRequestOrResponseDto rescindGroupInviteOfferOrRequest(Users actingUser, GroupInviteRequestOrResponseDto dto);

    void userLeaveGroup(Users user, Long groupId);
}
