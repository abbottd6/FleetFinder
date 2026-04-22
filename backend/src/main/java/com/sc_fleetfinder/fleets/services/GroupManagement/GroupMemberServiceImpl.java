package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.*;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.GroupManagement.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class GroupMemberServiceImpl implements GroupMemberService{

    protected final GroupMemberRepository memberRepo;
    protected final InGroupRankService rankService;
    protected final PushSubscriptionRepository pushSubRepo;
    protected final GroupListingRepository glr;
    protected final GroupInviteRepository inviteRepo;
    protected final CrewPositionRepository cpr;
    protected final ModelMapper modelMapper;
    protected final GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository;
    protected final ApplicationEventPublisher eventPublisher;
    protected final UserRepository userRepo;
    protected final CrewRoleClassificationRepository roleRepo;

    protected GroupMemberServiceImpl(GroupMemberRepository memberRepo, InGroupRankService rankService,
                           PushSubscriptionRepository pushSubRepo, GroupListingRepository glr,
                           GroupInviteRepository inviteRepo, CrewPositionRepository cpr,
                           ModelMapper modelMapper, GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository,
                           ApplicationEventPublisher eventPublisher, UserRepository userRepo,
                           CrewRoleClassificationRepository roleRepo) {
        this.memberRepo = memberRepo;
        this.rankService = rankService;
        this.pushSubRepo = pushSubRepo;
        this.glr = glr;
        this.inviteRepo = inviteRepo;
        this.cpr = cpr;
        this.modelMapper = modelMapper;
        this.assignedPrivilegeRepository = assignedPrivilegeRepository;
        this.eventPublisher = eventPublisher;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public void throwIfUserIsAlreadyAMember(Users user, Long listingId) {
        GroupMemberId id = new GroupMemberId(listingId, user.getUserId());
        if(memberRepo.findById(id).isPresent()) {
            throw new IllegalArgumentException("already a member of this group.");
        }
    }

    @Override
    public void userDismissInvite(Users user, Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Invite", inviteId));

        if(Objects.equals(invite.getRecipient().getUserId(), user.getUserId())) {
            if(invite.getSenderDismissed()) {
                inviteRepo.delete(invite);
            } else {
                invite.setRecipientDismissed(true);
                inviteRepo.save(invite);
            }
        } else if(rankService.verifyUserRankPermissions(user, invite.getGroupListing(), RankPrivilegeOptions.MANAGE_ROSTERS)) {
            if (invite.getRecipientDismissed()) {
                inviteRepo.delete(invite);
            } else {
                invite.setSenderDismissed(true);
                inviteRepo.save(invite);
            }
        } else {
            throw new ActionNotAuthorizedException(user.getUserId(), "dismiss", "Group Invite", inviteId);
        }
    }

    @Override
    public Boolean verifyUserIsAuthorizedMember(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        return this.rankService.verifyUserRankPermissions(user, listing, action);
    }

    @Override
    public Optional<CrewPosition> findGroupMemberCrewPosition(GroupMember member) {
        return cpr.findMemberPositionByAssignedMemberIdAndListingId(member, member.getGroupListing().getGroupId());
    }

    @Override
    public boolean hasGroupManagementPrivileges(InGroupRank rank) {
        if(rank == null) return false;
        List<RankPrivilegeOptions> privileges = assignedPrivilegeRepository.getAssignedPrivilegesByRank(rank);

        if(privileges.isEmpty()) return false;

        Set<RankPrivilegeOptions> managementPrivileges = Set.of(
                RankPrivilegeOptions.MANAGE_RANKS,
                RankPrivilegeOptions.MANAGE_POSITIONS,
                RankPrivilegeOptions.MANAGE_ROLES,
                RankPrivilegeOptions.MANAGE_ROSTERS,
                RankPrivilegeOptions.MANAGE_SUBGROUPS
        );

        return privileges.stream().anyMatch(managementPrivileges::contains);
    }

    @Override
    @Transactional
    public void createOwnerMember(Users user, GroupListing listing) {
        InGroupRank ownerRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Owner);
        Boolean hasExtNotes = getNewMemberHasExternalNotes(user);

        memberRepo.save(new GroupMember(listing, user, GroupMemberStatus.ACTIVE,
                ownerRank, hasExtNotes));
    }

    @Override
    @Transactional
    public GroupInviteRequestOrResponseDto declineGroupInviteOfferOrRequest(Users actingUser, GroupInviteRequestOrResponseDto dto) {
        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        if(Objects.equals(invite.getInviteDirection(), InviteDirection.OFFER)) {
            if(!Objects.equals(actingUser.getUserId(), dto.getRecipientSummary().getUserId())) {
                throw new ActionNotAuthorizedException(actingUser.getUserId(), "Accept Group Invite",
                        "Group Invite", dto.getInviteId());
            } else {
                invite.setInviteStatus(GroupInvitationStatus.DECLINED);
                invite.setActive(null);
                inviteRepo.save(invite);
            }
        } else if(Objects.equals(invite.getInviteDirection(), InviteDirection.REQUEST)) {
            GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                            .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingDetails().getGroupId()));

            rankService.verifyUserRankPermissions(actingUser, listing,
                    RankPrivilegeOptions.MANAGE_ROSTERS);

            invite.setInviteStatus(GroupInvitationStatus.DECLINED);
            invite.setActive(null);
            inviteRepo.save(invite);

            //TODO save outbox notification for recipient
        }

        return modelMapper.map(invite, GroupInviteRequestOrResponseDto.class);
    }

    @Override
    @Transactional
    public GroupInviteRequestOrResponseDto rescindGroupInviteOfferOrRequest(Users actingUser, GroupInviteRequestOrResponseDto dto) {
        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        if(Objects.equals(invite.getInviteDirection(), InviteDirection.OFFER)) {
            if(!Objects.equals(actingUser.getUserId(), dto.getRecipientSummary().getUserId())) {
                throw new ActionNotAuthorizedException(actingUser.getUserId(), "Accept Group Invite",
                        "Group Invite", dto.getInviteId());
            } else {
                invite.setInviteStatus(GroupInvitationStatus.RESCINDED);
                invite.setActive(null);
                inviteRepo.save(invite);
                //TODO save outbox notification for recipient
            }
        } else if(Objects.equals(invite.getInviteDirection(), InviteDirection.REQUEST)) {
            GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingDetails().getGroupId()));

            rankService.verifyUserRankPermissions(actingUser, listing,
                    RankPrivilegeOptions.MANAGE_ROSTERS);

            invite.setInviteStatus(GroupInvitationStatus.RESCINDED);
            invite.setActive(null);
            inviteRepo.save(invite);
            //TODO save outbox notification for recipient
        }

        return modelMapper.map(invite, GroupInviteRequestOrResponseDto.class);
    }

    @Override
    public Boolean getNewMemberHasExternalNotes(Users newMember) {
        Boolean hasExtNotes = newMember.getExternalGroupNotesEnabled();

        if(hasExtNotes == false) {
            hasExtNotes = pushSubRepo.getSetOfPushSubscriptionsByUser(newMember).stream()
                    .anyMatch(PushSubscription::getGroupNotesEnabled);
        }

        return hasExtNotes;
    }
}
