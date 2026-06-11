package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.*;
import com.sc_fleetfinder.fleets.DAO.PushSubscriptionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.DuplicateEntryException;
import com.sc_fleetfinder.fleets.exceptions.InviteStateConflictException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
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
    protected final GroupListingService gls;
    protected final GroupInviteRepository inviteRepo;
    protected final CrewPositionRepository cpr;
    protected final ModelMapper modelMapper;
    protected final GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository;
    protected final ApplicationEventPublisher eventPublisher;
    protected final UserService userService;
    protected final CrewRoleClassificationRepository roleRepo;

    protected GroupMemberServiceImpl(GroupMemberRepository memberRepo, InGroupRankService rankService,
                           PushSubscriptionRepository pushSubRepo, GroupListingService gls,
                           GroupInviteRepository inviteRepo, CrewPositionRepository cpr,
                           ModelMapper modelMapper, GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository,
                           ApplicationEventPublisher eventPublisher, UserService userService,
                           CrewRoleClassificationRepository roleRepo) {
        this.memberRepo = memberRepo;
        this.rankService = rankService;
        this.pushSubRepo = pushSubRepo;
        this.gls = gls;
        this.inviteRepo = inviteRepo;
        this.cpr = cpr;
        this.modelMapper = modelMapper;
        this.assignedPrivilegeRepository = assignedPrivilegeRepository;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
        this.roleRepo = roleRepo;
    }

    @Override
    public void throwIfUserIsAlreadyAMember(Users user, Long listingId) {
        GroupMemberId id = new GroupMemberId(listingId, user.getUserId());
        if(memberRepo.findById(id).isPresent()) {
            throw new DuplicateEntryException("already a member of this group.");
        }
    }

    @Override
    @Transactional
    public void userDismissInvite(Users user, Long inviteId) {
        GroupInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Group Invite", inviteId));

        if(Objects.equals(invite.getRecipient().getUserId(), user.getUserId())) {
            if (invite.getSenderDismissed()) {
                inviteRepo.delete(invite);
            } else {
                invite.setRecipientDismissed(true);
                inviteRepo.save(invite);
            }
        } else if(Objects.equals(invite.getSender().getUserId(), user.getUserId())) {
            if (invite.getRecipientDismissed()) {
                inviteRepo.delete(invite);
            } else {
                invite.setSenderDismissed(true);
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
    @Transactional(readOnly = true)
    public Boolean verifyUserIsAuthorizedManager(Users user, Long listingId) {
        GroupListing listing = gls.findGroupListingEntityById(listingId);

        RankPrivilegeOptions action = RankPrivilegeOptions.MANAGE_ROSTERS;

        return this.rankService.verifyUserRankPermissions(user, listing, action);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupMember verifyAndReturnUserAsGroupMember(Users user, Long listingId) {
        GroupListing listing = gls.findGroupListingEntityById(listingId);

        return memberRepo.findByUserUserIdAndGroupListing(user.getUserId(), listing)
                .orElseThrow(() -> new ResourceNotFoundException("Group Member", user.getUserId(), listingId));
    }

    @Override
    public Optional<CrewPosition> findGroupMemberCrewPosition(GroupMember member) {
        if(member == null) {
            return Optional.empty();
        }
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

        GroupMember owner = new GroupMember(listing, user, GroupMemberStatus.ACTIVE,
                ownerRank.getRankId(), hasExtNotes);

        String comms = listing.getCommsOption().toLowerCase();

        if(comms.equals("required") || comms.equals("optional")) {
            owner.setHasHeadset(true);
            owner.setHasMic(true);
        }

        memberRepo.save(owner);
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

    @Override
    public void evaluateForInviteStatusConflict(GroupInvite invite) {
        if (invite.getInviteStatus() != GroupInviteStatus.PENDING) {
            throw new InviteStateConflictException(invite.getInviteStatus(),
                    GroupInviteStatus.RESCINDED, invite.getInviteId());
        }
    }
}
