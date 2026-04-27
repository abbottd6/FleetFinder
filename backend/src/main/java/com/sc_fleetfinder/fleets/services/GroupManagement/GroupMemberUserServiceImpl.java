package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.*;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.MemberPositionSummaryDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
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
import java.util.Objects;

@Slf4j
@Service
public class GroupMemberUserServiceImpl extends GroupMemberServiceImpl implements GroupMemberUserService {

    protected GroupMemberUserServiceImpl(GroupMemberRepository memberRepo, InGroupRankService rankService,
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
    public Page<GroupMembershipResponseDto> getMyGroupMemberships(Users user, SortablePageRequestDto pageDto) {
        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize());

        Page<GroupMember> myMemberships;

        if(Objects.equals(pageDto.getSortField(), "nearest")) {
            myMemberships = memberRepo.findAllByUserOrderByEventTimeProximity(user.getUserId(), pageable);
        } else {
            myMemberships = memberRepo.findAllByUserOrderByCreatedAtDesc(user, pageable);
        }

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

    @Override
    public Page<GroupInviteRequestOrResponseDto> getMyGroupInvites(Users user, GenericPageRequestDto pageDto) {
        Pageable pageable =  PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());
        Page<GroupInvite> myInvites = inviteRepo.findInvitesByUserId(user.getUserId(), pageable);

        return myInvites.map(inv -> modelMapper.map(inv, GroupInviteRequestOrResponseDto.class));
    }

    @Override
    public Page<GroupListingResponseDto> getMyInviteAuthorizedMemberships(Users user) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<GroupListing> myAuthorized = rankService.getMyInviteAuthorizedGroups(user.getUserId(), pageable);

        return myAuthorized.map(listing -> modelMapper.map(listing, GroupListingResponseDto.class));
    }

    @Override
    @Transactional
    public GroupInviteRequestOrResponseDto sendGroupInviteRequest(Users sender, SendGroupInviteRequestDto dto) {
        GroupListing listing = glr.findById(dto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing", dto.getListingId()));

        throwIfUserIsAlreadyAMember(sender, dto.getListingId());

        InviteDirection direction = InviteDirection.REQUEST;
        GroupInviteStatus pending = GroupInviteStatus.PENDING;
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

            eventPublisher.publishEvent(new NewGroupInviteOrRequestNotifyEvent(savedInv));

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
    public GroupInviteRequestOrResponseDto rescindGroupInviteRequest(Users sender, Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", inviteId));

        if(invite.getInviteStatus() != GroupInviteStatus.PENDING) {
            throw new InviteStateConflictException(invite.getInviteStatus(), GroupInviteStatus.RESCINDED, inviteId);
        }

        if (!Objects.equals(sender.getUserId(), invite.getSender().getUserId())) {
            throw new ActionNotAuthorizedException(sender.getUserId(), "Accept",
                    "Group Invite", inviteId);
        }

        invite.setInviteStatus(GroupInviteStatus.RESCINDED);
        invite.setActive(null);
        GroupInvite saved = inviteRepo.save(invite);

        //TODO save outbox notification for recipient??

        return modelMapper.map(saved, GroupInviteRequestOrResponseDto.class);
    }

    @Override
    @Transactional
    public GroupMembershipResponseDto acceptGroupInviteOffer(Users newMember, GroupInviteRequestOrResponseDto dto) {
        GroupListing listing = glr.findById(dto.getListingDetails().getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group Listing", dto.getListingDetails().getGroupId()));

        throwIfUserIsAlreadyAMember(newMember, listing.getGroupId());

        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        if(invite.getInviteStatus().isTerminal()) {
            throw new InviteStateConflictException(invite.getInviteStatus(), dto.getInviteStatus(),
                    invite.getInviteId());
        }

        Users sender = userRepo.findById(dto.getSenderSummary().getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("Sender User", dto.getSenderSummary().getUserId())
        );

        rankService.verifyUserRankPermissions(sender, listing, RankPrivilegeOptions.INVITE);

        //TODO do something with this or remove it?
        Boolean hasComms = null;

        Boolean hasExtNotes = getNewMemberHasExternalNotes(newMember);

        InGroupRank newMemberRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Member);

        try {
            GroupMember savedMember = memberRepo.saveAndFlush(new GroupMember(listing, newMember, dto.getMemberStatus(), newMemberRank,
                    hasComms, hasExtNotes));

            invite.setInviteStatus(GroupInviteStatus.ACCEPTED);
            invite.setActive(null);
            inviteRepo.save(invite);

            eventPublisher.publishEvent(new NewGroupMemberNotifyEvent(listing.getUsers(), invite, savedMember));

            if(!Objects.equals(invite.getSender().getUserId(), listing.getUsers().getUserId())) {
                eventPublisher.publishEvent(new NewGroupMemberNotifyEvent(invite.getSender(), invite, savedMember));
            }

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
    public GroupInviteRequestOrResponseDto declineGroupInviteOffer(Users actingUser, Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", inviteId));

        evaluateForInviteStatusConflict(invite);

        if (!Objects.equals(invite.getRecipient().getUserId(), actingUser.getUserId())) {
            throw new ActionNotAuthorizedException(actingUser.getUserId(), "Accept Group Invite",
                    "Group Invite", inviteId);
        }

        invite.setInviteStatus(GroupInviteStatus.DECLINED);
        invite.setActive(null);
        GroupInvite saved = inviteRepo.save(invite);

        return modelMapper.map(saved, GroupInviteRequestOrResponseDto.class);
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
}
