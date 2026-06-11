package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.*;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
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
import com.sc_fleetfinder.fleets.events.GroupManagement.GroupMemberLeftNotifyEvent;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewGroupMemberNotifyEvent;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewGroupInviteOrRequestNotifyEvent;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.DuplicateEntryException;
import com.sc_fleetfinder.fleets.exceptions.InviteStateConflictException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.utils.GroupManagement.*;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
@Service
public class GroupMemberUserServiceImpl extends GroupMemberServiceImpl implements GroupMemberUserService {

    protected GroupMemberUserServiceImpl(GroupMemberRepository memberRepo, InGroupRankService rankService,
                                               PushSubscriptionRepository pushSubRepo, GroupListingService gls,
                                               GroupInviteRepository inviteRepo, CrewPositionRepository cpr,
                                               ModelMapper modelMapper,
                                               GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository,
                                               CrewRoleClassificationRepository roleRepo,
                                               UserService userService,
                                               ApplicationEventPublisher eventPublisher) {
        super(memberRepo, rankService, pushSubRepo, gls, inviteRepo, cpr, modelMapper, assignedPrivilegeRepository,
                eventPublisher, userService, roleRepo);
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
        GroupListing listing = gls.findGroupListingEntityById(dto.getListingId());

        throwIfUserIsAlreadyAMember(sender, dto.getListingId());

        InviteDirection direction = InviteDirection.REQUEST;
        GroupInviteStatus pending = GroupInviteStatus.PENDING;
        CrewRoleClassification role = null;
        Users recipient = listing.getUsers();

        if(!Objects.equals(sender.getInGameUsername(), dto.getInGameUsername())) {
            sender.setInGameUsername(dto.getInGameUsername());
            userService.saveAndFlush(sender);
        }

        Instant expiresAt = listing.getEventSchedule();
        if(expiresAt == null) {
            expiresAt = Instant.now().plus(12, ChronoUnit.HOURS);
        }
        try {
            GroupInvite savedInv = inviteRepo.save(new GroupInvite(sender, recipient, listing, direction,
                    dto.getHasMic(), dto.getMemberStatus(), dto.getHasHeadset(), role,
                    pending, dto.getRequestMessage(), expiresAt));

            eventPublisher.publishEvent(new NewGroupInviteOrRequestNotifyEvent(savedInv));

            return modelMapper.map(savedInv, GroupInviteRequestOrResponseDto.class);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cve &&
                    cve.getConstraintName() != null &&
                    cve.getConstraintName().contains("uq_group_invite_on_direction_users_listing_and_active")) {
                throw new DuplicateEntryException("An invite request has already been sent for this group.");
            } else {
                throw new InternalServerErrorException(e.getMessage());
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
        // no thanks

        return modelMapper.map(saved, GroupInviteRequestOrResponseDto.class);
    }

    @Override
    @Transactional
    public GroupMembershipResponseDto acceptGroupInviteOffer(Users newMember, GroupInviteRequestOrResponseDto dto) {
        GroupListing listing = gls.findGroupListingEntityById(dto.getListingDetails().getGroupId());

        memberRepo.findByUserUserIdAndGroupListing(newMember.getUserId(), listing)
                .ifPresent(member -> {
                    if(dto.getMemberStatus() == GroupMemberStatus.ACTIVE
                            && member.getMemberStatus() == GroupMemberStatus.WAITLIST) {
                        deleteMemberForWaitlistToActiveConversion(member);
                    } else {
                        throw new DuplicateEntryException("already a member of this group.");
                    }
                });

        GroupInvite invite = inviteRepo.findById(dto.getInviteId()).orElseThrow(() -> new ResourceNotFoundException(
                "Group Invite", dto.getInviteId()));

        if(invite.getInviteStatus().isTerminal()) {
            throw new InviteStateConflictException(invite.getInviteStatus(), dto.getInviteStatus(),
                    invite.getInviteId());
        }

        Users sender = userService.findUserById(dto.getSenderSummary().getUserId());

        rankService.verifyUserRankPermissions(sender, listing, RankPrivilegeOptions.INVITE);

        Boolean hasMic = dto.getHasMic();
        Boolean hasHeadset = dto.getHasHeadset();

        Boolean hasExtNotes = getNewMemberHasExternalNotes(newMember);

        if(!Objects.equals(dto.getRecipientSummary().getInGameUsername(), newMember.getInGameUsername())) {
            newMember.setInGameUsername(dto.getRecipientSummary().getInGameUsername());
            userService.saveAndFlush(newMember);
        }

        InGroupRank newMemberRank = rankService.getGenericRankByTitle(GroupRankGenericTypes.Member);

        try {
            GroupMember savedMember = memberRepo.saveAndFlush(new GroupMember(listing, newMember, hasMic,
                    dto.getMemberStatus(), hasHeadset, newMemberRank.getRankId(), hasExtNotes));

            invite.setInviteStatus(GroupInviteStatus.ACCEPTED);
            invite.setActive(null);
            inviteRepo.save(invite);

            eventPublisher.publishEvent(new NewGroupMemberNotifyEvent(listing.getUsers(), invite, savedMember));

            if(!Objects.equals(invite.getSender().getUserId(), listing.getUsers().getUserId())) {
                eventPublisher.publishEvent(new NewGroupMemberNotifyEvent(invite.getSender(), invite, savedMember));
            }

            listing.setCurrentPartySize(listing.getCurrentPartySize() + 1);
            gls.saveListing(listing);

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void deleteMemberForWaitlistToActiveConversion(GroupMember waitlistMember) {
        if(waitlistMember.getMemberStatus() != GroupMemberStatus.WAITLIST) {
            throw new IllegalArgumentException("Cannot delete waitlist member for conversion to active when " +
                    "member is not in waitlist.");
        }

        memberRepo.delete(waitlistMember);
        memberRepo.flush();
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
        GroupListing listing = gls.findGroupListingEntityById(groupId);

        GroupMember deletedMember = memberRepo.findByUserUserIdAndGroupListing(actingUser.getUserId(), listing)
                .orElseThrow(() -> new ResourceNotFoundException("Group Membership", actingUser.getUserId(), groupId));

        memberRepo.delete(deletedMember);

        inviteRepo.deleteByUserAndGroupListing(
                actingUser.getUserId(), listing.getGroupId());

        listing.setCurrentPartySize(listing.getCurrentPartySize() - 1);
        gls.saveListing(listing);

        Instant now = Instant.now();
        if((listing.getEventSchedule() != null) && (now.isBefore(listing.getEventSchedule().plus(1, ChronoUnit.HOURS)))) {
            eventPublisher.publishEvent(new GroupMemberLeftNotifyEvent(deletedMember, listing));
        } else if(listing.getGroupStatus().getGroupStatus().equals("Current/Live")
                && now.isBefore(listing.getCreationTimestamp().plus(2, ChronoUnit.HOURS))) {
            eventPublisher.publishEvent(new GroupMemberLeftNotifyEvent(deletedMember, listing));
        }
    }
}
