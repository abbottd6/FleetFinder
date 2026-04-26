package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.*;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewGroupMemberNotifyEvent;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewGroupInviteOrRequestNotifyEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.DuplicateEntryException;
import com.sc_fleetfinder.fleets.exceptions.InviteStateConflictException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.utils.GroupManagement.*;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@Service
@Slf4j
public class GroupMemberManagementServiceImpl extends GroupMemberServiceImpl implements GroupMemberManagementService {

    protected GroupMemberManagementServiceImpl(GroupMemberRepository memberRepo, InGroupRankService rankService,
                                               PushSubscriptionRepository pushSubRepo, GroupListingRepository glr,
                                               GroupInviteRepository inviteRepo, CrewPositionRepository cpr,
                                               ModelMapper modelMapper,
                                               GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository,
                                               CrewRoleClassificationRepository roleRepo,
                                               UserRepository userRepo,
                                               ApplicationEventPublisher eventPublisher) {
        super(memberRepo, rankService, pushSubRepo, glr, inviteRepo, cpr, modelMapper, assignedPrivilegeRepository,
                eventPublisher, userRepo, roleRepo);
    }


    @Override
    public Page<GroupManagerMemberResponseDto> getActiveRosterGroupMembers(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        this.rankService.verifyUserRankPermissions(user, listing, action);

        Pageable pageable = PageRequest.of(0, 100);

        Page<GroupMember> memberEntities = this.memberRepo.findActiveRosterMembersByGroup(listingId, pageable);

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
    public Page<GroupManagerMemberResponseDto> getWaitlistMembers(Users user, Long listingId) {
        GroupListing listing = glr.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", listingId));

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        this.rankService.verifyUserRankPermissions(user, listing, action);

        Pageable pageable = PageRequest.of(0, 100);

        Page<GroupMember> memberEntities = this.memberRepo.findWaitlistMembersByGroup(listingId, pageable);

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

        Page<GroupInvite> inviteEntities = this.inviteRepo.findPageOfAllGroupInvitesByGroupId(listingId, pageable);

        return inviteEntities.map(inv -> modelMapper.map(inv, GroupManagerInviteResponseDto.class));
    }

    @Override
    @Transactional
    public GroupManagerMemberResponseDto provisionNewGroupMember_ActiveOrWaitlist(Users actingUser, GroupManagerInviteResponseDto dto) {
        GroupListing listing = glr.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group Listing", dto.getListingId()));

        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        Users newMember = invite.getSender();

        throwIfUserIsAlreadyAMember(newMember, listing.getGroupId());

        rankService.verifyUserRankPermissions(actingUser, listing, RankPrivilegeOptions.MANAGE_ROSTERS);

        //TODO do something with this or remove it?
        Boolean hasComms = null;
        Boolean hasExtNotes = getNewMemberHasExternalNotes(newMember);
        InGroupRank newMemberRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Member);

        try {
            GroupMember savedMember = memberRepo.save(new GroupMember(listing, newMember, dto.getMemberStatus(), newMemberRank,
                    hasComms, hasExtNotes));

            invite.setInviteStatus(GroupInviteStatus.ACCEPTED);
            invite.setActive(null);
            inviteRepo.save(invite);

            eventPublisher.publishEvent(new NewGroupMemberNotifyEvent(newMember, invite, savedMember));

            if(savedMember.getMemberStatus() == GroupMemberStatus.ACTIVE) {
                listing.setCurrentPartySize(listing.getCurrentPartySize() + 1);
                glr.save(listing);
            }

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
    public GroupManagerInviteResponseDto declineGroupInviteRequest(Users actingUser, Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", inviteId));

        evaluateForInviteStatusConflict(invite);

        rankService.verifyUserRankPermissions(actingUser, invite.getGroupListing(),
                RankPrivilegeOptions.MANAGE_ROSTERS);

        invite.setInviteStatus(GroupInviteStatus.DECLINED);
        invite.setActive(null);
        GroupInvite saved = inviteRepo.save(invite);

        // TODO send notification?

        return modelMapper.map(saved, GroupManagerInviteResponseDto.class);
    }

    @Override
    @Transactional
    public GroupManagerInviteResponseDto mirrorJoinRequestForActiveRosterToWaitlistInvite(Users actingUser,
                                                                                           Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", inviteId));

        evaluateForInviteStatusConflict(invite);

        rankService.verifyUserRankPermissions(actingUser, invite.getGroupListing(),
                RankPrivilegeOptions.MANAGE_ROSTERS);

        inviteRepo.setExistingWaitlistInviteToRescinded(invite.getSender(),
                invite.getGroupListing().getGroupId(), InviteDirection.OFFER);

        invite.setInviteStatus(GroupInviteStatus.DECLINED);
        invite.setActive(null);
        inviteRepo.save(invite);

        String message = "You've been offered a waitlist position in response to your join request.";
        UserMonikerSummary recipientSummary = modelMapper.map(invite.getSender(), UserMonikerSummary.class);


        GroupRoleSummaryDto waitlistRoleSummary = modelMapper.map(Optional.ofNullable(invite.getInviteRole()), GroupRoleSummaryDto.class);


        SendGroupInviteOfferDto waitlistOfferDto = new SendGroupInviteOfferDto(invite,
                GroupMemberStatus.WAITLIST, recipientSummary, waitlistRoleSummary, message);

        return sendGroupInviteOffer(actingUser, waitlistOfferDto);
    }

    @Override
    @Transactional
    public GroupManagerInviteResponseDto sendGroupInviteOffer(Users sender, SendGroupInviteOfferDto dto) {
        GroupListing listing = glr.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupListing", dto.getListingId()));

        Boolean groupActionAuth = this.rankService.verifyUserRankPermissions(
                sender, listing, RankPrivilegeOptions.INVITE);

        if (groupActionAuth != true) {
            throw new ActionNotAuthorizedException(sender.getUserId(), RankPrivilegeOptions.INVITE.toString(),
                    "Group Management", dto.getListingId());
        } else {
            InviteDirection direction = InviteDirection.OFFER;
            GroupInviteStatus pending = GroupInviteStatus.PENDING;

            Instant expiresAt = dto.getExpiresAt();

            if (expiresAt == null) {
                if (listing.getEventSchedule() != null) {
                    expiresAt = listing.getEventSchedule();
                } else {
                    expiresAt = Instant.now().plus(12, ChronoUnit.HOURS);
                }
            }


            CrewRoleClassification role = Optional.ofNullable(dto.getRoleSummary())
                    .flatMap(r -> roleRepo.findById(r.getRoleId()))
                    .orElse(null);

            Users recipient = userRepo.findById(dto.getRecipientSummary().getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", dto.getRecipientSummary().getUserId()));

            throwIfUserIsAlreadyAMember(recipient, dto.getListingId());

            try {
                GroupInvite savedInvite = inviteRepo.save(new GroupInvite(sender, recipient, listing, direction,
                        dto.getMemberStatus(), role, pending, dto.getInviteMessage(), expiresAt));

                eventPublisher.publishEvent(new NewGroupInviteOrRequestNotifyEvent(savedInvite));

                return modelMapper.map(savedInvite, GroupManagerInviteResponseDto.class);

            } catch (DataIntegrityViolationException e) {
                if (e.getCause() instanceof ConstraintViolationException cve &&
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
    public GroupManagerInviteResponseDto rescindGroupInviteOffer(Users manager, Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", inviteId));

        evaluateForInviteStatusConflict(invite);

        rankService.verifyUserRankPermissions(manager, invite.getGroupListing(), RankPrivilegeOptions.MANAGE_ROSTERS);

        invite.setInviteStatus(GroupInviteStatus.RESCINDED);
        invite.setActive(null);
        GroupInvite saved = inviteRepo.save(invite);

        //TODO save outbox notification for recipient

        return modelMapper.map(saved, GroupManagerInviteResponseDto.class);
    }
}