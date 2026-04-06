package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;

public interface GroupMemberService {

    GroupMember createOwnerMember(Users user, GroupListing listing);

    Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user);

    GroupInviteResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto);

    GroupInviteResponseDto sendGroupInviteOffer(Users sender, SendGroupInviteOfferDto dto);
}
