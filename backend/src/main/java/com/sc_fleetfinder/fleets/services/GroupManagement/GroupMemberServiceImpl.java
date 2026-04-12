package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.*;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.MemberPositionSummaryDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewInviteRequestEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.DuplicateEntryException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.GroupManagement.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService{

    private final GroupMemberRepository memberRepo;
    private final InGroupRankService rankService;
    private final PushSubscriptionRepository pushSubRepo;
    private final GroupListingRepository glr;
    private final GroupInviteRepository inviteRepo;
    private final CrewRoleClassificationRepository roleRepo;
    private final CrewPositionRepository cpr;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository;

    @Override
    public Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<GroupMember> myMemberships = memberRepo.findAllByUser(user, pageable);

        return myMemberships.map(m -> {
            GroupMembershipResponseDto dto = modelMapper.map(m, GroupMembershipResponseDto.class);
            MemberPositionSummaryDto positionSummaryDto = findGroupMemberCrewPosition(m)
                    .map(cp -> modelMapper.map(cp, MemberPositionSummaryDto.class))
                    .orElse(null);
            Boolean isAuthorized = hasGroupManagementPrivileges(m.getMemberRank());
            dto.setIsAuthorizedManager(isAuthorized);
            dto.setMemberRole(positionSummaryDto);
            return dto;
        });
    }

    private Optional<CrewPosition> findGroupMemberCrewPosition(GroupMember member) {
        return cpr.findMemberPositionByAssignedMemberIdAndListingId(member, member.getGroupListing().getGroupId());
    }

    private boolean hasGroupManagementPrivileges(InGroupRank rank) {
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
    public GroupInviteRequestOrResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto) {
        GroupListing listing = glr.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingId()));

        InviteDirection direction = InviteDirection.REQUEST;
        GroupInvitationStatus pending = GroupInvitationStatus.PENDING;
        CrewRoleClassification role = null;
        Users recipient = listing.getUsers();

        if(!Objects.equals(sender.getInGameUsername(), dto.getInGameUsername())) {
            sender.setInGameUsername(dto.getInGameUsername());
            userRepo.saveAndFlush(sender);
        }

        Instant expiresAt = listing.getEventSchedule();
        if(expiresAt == null) {
            expiresAt = Instant.now().plus(12, ChronoUnit.HOURS);
        }
        try {
            GroupInvite savedInv = inviteRepo.save(new GroupInvite(sender, recipient, listing, direction,
                    dto.getMemberStatus(), role, pending, dto.getRequestMessage(), expiresAt));

            eventPublisher.publishEvent(new NewInviteRequestEvent(savedInv));

            return modelMapper.map(savedInv, GroupInviteRequestOrResponseDto.class);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cve &&
                    cve.getConstraintName() != null &&
                    cve.getConstraintName().contains("uq_group_invite_type_sender_recipient_group")) {
                        throw new DuplicateEntryException("An invite request has already been sent for this group.");
            } else {
                throw e;
            }
        }
    }

    @Override
    @Transactional
    public GroupInviteRequestOrResponseDto sendGroupInviteOffer(Users sender, SendGroupInviteOfferDto dto) {
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

            try {
                GroupInvite savedInvite = inviteRepo.save(new GroupInvite(sender, recipient, listing, direction,
                        dto.getMemberStatus(), role, pending, dto.getInviteMessage(), expiresAt));

                eventPublisher.publishEvent(new NewInviteRequestEvent(savedInvite));

                return modelMapper.map(savedInvite, GroupInviteRequestOrResponseDto.class);

            } catch (DataIntegrityViolationException e) {
                if(e.getCause() instanceof ConstraintViolationException cve &&
                    cve.getConstraintName() != null &&
                    cve.getConstraintName().contains("uq_group_invite_type_sender_recipient_group")) {
                    throw new DuplicateEntryException("An invite has already been sent to this user for this group.");
                } else {
                    throw e;
                }
            }
        }
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
    public GroupMembershipResponseDto acceptGroupInviteOffer(Users newMember, GroupInviteRequestOrResponseDto dto) {
        GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group Listing", dto.getListingDetails().getGroupId()));

        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        Users sender = userRepo.findById(dto.getSenderSummary().getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("Sender User", dto.getSenderSummary().getUserId())
        );

        rankService.verifyUserRankPermissions(sender, listing, RankPrivilegeOptions.INVITE);

        //TODO do something with this or remove it?
        Boolean hasComms = null;

        Boolean hasExtNotes = getNewMemberHasExternalNotes(newMember);

        InGroupRank newMemberRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Member);

        try {
            GroupMember savedMember = memberRepo.save(new GroupMember(listing, newMember, dto.getMemberStatus(), newMemberRank,
                    hasComms, hasExtNotes));

            invite.setInviteStatus(GroupInvitationStatus.ACCEPTED);
            inviteRepo.save(invite);

            //TODO save outbox notification for recipient

            listing.setCurrentPartySize(listing.getCurrentPartySize() + 1);
            glr.save(listing);

            return modelMapper.map(savedMember, GroupMembershipResponseDto.class);

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve &&
                    cve.getConstraintName() != null &&
                    cve.getConstraintName().contains("PRIMARY")) {
                throw new DuplicateEntryException("This user has already joined the group.");
            } else {
                throw e;
            }
        }
    }

    @Override
    @Transactional
    public GroupManagerMemberResponseDto acceptGroupInviteRequest(Users actingUser, GroupInviteRequestOrResponseDto dto) {
        GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group Listing", dto.getListingDetails().getGroupId()));

        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        Users newMember = invite.getRecipient();

        rankService.verifyUserRankPermissions(actingUser, listing, RankPrivilegeOptions.MANAGE_ROSTERS);

        //TODO do something with this or remove it?
        Boolean hasComms = null;
        Boolean hasExtNotes = getNewMemberHasExternalNotes(newMember);
        InGroupRank newMemberRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Member);

        try {
            GroupMember savedMember = memberRepo.save(new GroupMember(listing, newMember, dto.getMemberStatus(), newMemberRank,
                    hasComms, hasExtNotes));

            invite.setInviteStatus(GroupInvitationStatus.ACCEPTED);
            inviteRepo.save(invite);

            //TODO save outbox notification for recipient
            listing.setCurrentPartySize(listing.getCurrentPartySize() + 1);
            glr.save(listing);

            return modelMapper.map(savedMember, GroupManagerMemberResponseDto.class);

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve &&
                    cve.getConstraintName() != null &&
                    cve.getConstraintName().contains("PRIMARY")) {
                throw new DuplicateEntryException("This user has already joined the group.");
            } else {
                throw e;
            }
        }
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
                inviteRepo.save(invite);
            }
        } else if(Objects.equals(invite.getInviteDirection(), InviteDirection.REQUEST)) {
            GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                            .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingDetails().getGroupId()));

            rankService.verifyUserRankPermissions(actingUser, listing,
                    RankPrivilegeOptions.MANAGE_ROSTERS);

            invite.setInviteStatus(GroupInvitationStatus.DECLINED);
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
                inviteRepo.save(invite);
                //TODO save outbox notification for recipient
            }
        } else if(Objects.equals(invite.getInviteDirection(), InviteDirection.REQUEST)) {
            GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingDetails().getGroupId()));

            rankService.verifyUserRankPermissions(actingUser, listing,
                    RankPrivilegeOptions.MANAGE_ROSTERS);

            invite.setInviteStatus(GroupInvitationStatus.RESCINDED);
            inviteRepo.save(invite);
            //TODO save outbox notification for recipient
        }

        return modelMapper.map(invite, GroupInviteRequestOrResponseDto.class);
    }

    @Override
    @Transactional
    public void userLeaveGroup(Users actingUser, Long groupId) {
        GroupListing listing = glr.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", groupId));

        Integer deletedMember = memberRepo.deleteByUserAndGroupListing(actingUser, listing);

        inviteRepo.deleteByUserAndGroupListing(
                actingUser.getUserId(), listing.getGroupId());

        if(deletedMember == 0) {
            throw new ResourceNotFoundException("Group Membership", actingUser.getUserId(), groupId);
        }

        listing.setCurrentPartySize(listing.getCurrentPartySize() - 1);
        glr.save(listing);

        //TODO save outbox notification for group owner
    }

    private Boolean getNewMemberHasExternalNotes(Users newMember) {
        Boolean hasExtNotes = newMember.getExternalGroupNotesEnabled();

        if(hasExtNotes == false) {
            hasExtNotes = pushSubRepo.getSetOfPushSubscriptionsByUser(newMember).stream()
                    .anyMatch(PushSubscription::getGroupNotesEnabled);
        }

        return hasExtNotes;
    }

}
