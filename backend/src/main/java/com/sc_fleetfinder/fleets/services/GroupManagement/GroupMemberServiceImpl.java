package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewRoleClassificationRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupInviteRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupMemberRepository;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.GroupManagement.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService{

    private final GroupMemberRepository memberRepo;
    private final InGroupRankService rankService;
    private final PushSubscriptionRepository pushSubRepo;
    private final GroupListingRepository glr;
    private final GroupInviteRepository inviteRepo;
    private final CrewRoleClassificationRepository roleRepo;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    @Override
    public Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<GroupMember> myMemberships = memberRepo.findAllByUser(user, pageable);

        return myMemberships.map(m -> modelMapper.map(m, GroupMembershipResponseDto.class));
    }

    @Override
    public GroupInviteResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto) {
        GroupListing listing = glr.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingId()));

        InviteDirection direction = InviteDirection.REQUEST;
        GroupInvitationStatus pending = GroupInvitationStatus.PENDING;
        CrewRoleClassification role = null;

        Instant expiresAt = listing.getEventSchedule();
        if(expiresAt == null) {
            expiresAt = Instant.now().plus(12, ChronoUnit.HOURS);
        }

        GroupInvite invRequest = new GroupInvite(sender, listing.getUsers(), listing, direction,
                dto.getRosterClass(), role, pending, dto.getRequestMessage(), expiresAt);

        GroupInvite saved = inviteRepo.save(invRequest);

        return modelMapper.map(saved, GroupInviteResponseDto.class);
    }

    @Override
    public GroupInviteResponseDto sendGroupInviteOffer(Users sender, SendGroupInviteOfferDto dto) {
        GroupListing listing = glr.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupListing", dto.getListingId()));

        Boolean groupActionAuth = this.rankService.verifyUserRankPermissions(
                sender, listing, RankPrivilegeOptions.INVITE);

        if(groupActionAuth != true) {
            throw new ActionNotAuthorizedException(sender.getUserId(), RankPrivilegeOptions.INVITE.toString(),
                    "Group Management", dto.getListingId());
        } else {
            InviteDirection direction = InviteDirection.OFFER;
            GroupInvitationStatus pending = GroupInvitationStatus.PENDING;

            Instant expiresAt = dto.getExpiresAt();

            if(expiresAt == null) {
                if(listing.getEventSchedule() != null) {
                    expiresAt = listing.getEventSchedule();
                } else {
                    expiresAt = Instant.now().plus(12, ChronoUnit.HOURS);
                }
            }

            CrewRoleClassification role = roleRepo.findById(dto.getRoleSummary().getRoleId())
                    .orElse(null);

            Users recipient = userRepo.findById(dto.getRecipientSummary().getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", dto.getRecipientSummary().getUserId()));

            GroupInvite sendInvite = new GroupInvite(sender, recipient, listing, direction,
                    dto.getRosterClass(), role, pending, dto.getInviteMessage(), expiresAt);

            GroupInvite savedInvite = inviteRepo.save(sendInvite);

            return modelMapper.map(savedInvite, GroupInviteResponseDto.class);
        }
    }

    @Override
    public GroupMember createOwnerMember(Users user, GroupListing listing) {
        InGroupRank ownerRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Owner);
        Boolean hasExtNotes = user.getExternalGroupNotesEnabled();

        if(hasExtNotes == false) {
            hasExtNotes = pushSubRepo.getSetOfPushSubscriptionsByUser(user).stream()
                    .anyMatch(PushSubscription::getGroupNotesEnabled);
        }
        GroupMember owner = new GroupMember(listing, user, MemberStatusOptions.ACTIVE,
                ownerRank, hasExtNotes);

        return memberRepo.save(owner);
    }

}
