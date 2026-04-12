package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewPositionRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupInviteRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupMemberRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerInviteResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.MemberPositionSummaryDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberManagementServiceImpl implements GroupMemberManagementService {

    private final GroupMemberRepository groupMemberRepo;
    private final GroupInviteRepository groupInviteRepo;
    private final GroupListingRepository glr;
    private final InGroupRankService rankService;
    private final CrewPositionRepository cpr;
    private final ModelMapper modelMapper;

    @Override
    public Boolean verifyUserIsAuthorizedMember(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        return this.rankService.verifyUserRankPermissions(user, listing, action);
    }

    @Override
    public Page<GroupManagerMemberResponseDto> getActiveRosterGroupMembers(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        this.rankService.verifyUserRankPermissions(user, listing, action);

        Pageable pageable = PageRequest.of(0, 100);

        Page<GroupMember> memberEntities = this.groupMemberRepo.findActiveRosterMembersByGroup(listingId, pageable);

        return memberEntities.map(m -> {
            GroupManagerMemberResponseDto dto = modelMapper.map(m, GroupManagerMemberResponseDto.class);
            MemberPositionSummaryDto positionSummaryDto = findGroupMemberCrewPosition(m)
                    .map(cp -> modelMapper.map(cp, MemberPositionSummaryDto.class))
                    .orElse(null);

            dto.setMemberRole(positionSummaryDto);
            return dto;
        });
    }

    private Optional<CrewPosition> findGroupMemberCrewPosition(GroupMember m) {
        return cpr.findMemberPositionByAssignedMemberIdAndListingId(m, m.getGroupListing().getGroupId());
    }

    @Override
    public Page<GroupManagerMemberResponseDto> getWaitlistMembers(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        this.rankService.verifyUserRankPermissions(user, listing, action);

        Pageable pageable = PageRequest.of(0, 100);

        Page<GroupMember> memberEntities = this.groupMemberRepo.findWaitlistMembersByGroup(listingId, pageable);

        return memberEntities.map(m -> {
            GroupManagerMemberResponseDto dto = modelMapper.map(m, GroupManagerMemberResponseDto.class);
            MemberPositionSummaryDto positionSummaryDto = findGroupMemberCrewPosition(m)
                    .map(cp -> modelMapper.map(cp, MemberPositionSummaryDto.class))
                    .orElse(null);

            dto.setMemberRole(positionSummaryDto);
            return dto;
        });
    }

    @Override
    public Page<GroupManagerInviteResponseDto> getGroupInvitesPage(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        this.rankService.verifyUserRankPermissions(user, listing, action);

        Pageable pageable = PageRequest.of(0, 100);

        Page<GroupInvite> inviteEntities = this.groupInviteRepo.findPageOfAllGroupInvitesByGroupId(listingId, pageable);

        return inviteEntities.map(inv -> modelMapper.map(inv, GroupManagerInviteResponseDto.class));
    }

    @Override
    @Transactional
    public GroupManagerMemberResponseDto acceptGroupInviteRequest(Users user, Long listingId, Long inviteId) {
        return null;
    }

    @Override
    @Transactional
    public void declineGroupInviteRequest(Users user, Long listingId, Long inviteId) {

    }

    @Override
    @Transactional
    public GroupManagerInviteResponseDto sendGroupInviteOffer(Users sender, Long listingId, Long recipientId) {
        return null;
    }
}
