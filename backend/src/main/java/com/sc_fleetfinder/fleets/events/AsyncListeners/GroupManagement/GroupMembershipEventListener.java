package com.sc_fleetfinder.fleets.events.AsyncListeners.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupInviteRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupMemberRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.GroupManagement.*;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberManagementServiceImpl;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupMembershipEventListener {

    private final GroupInviteRepository inviteRepo;
    private final GroupMemberRepository memberRepo;
    private final GroupMemberManagementServiceImpl memberManagementService;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewListingCreateOwnerMember(NewListingCreateOwnerMember event) {
        memberManagementService.createOwnerMember(event.owner(), event.listing());
    }

    @Async
    @EventListener
    @Transactional
    public void handleNewInviteRequestNotify(NewGroupInviteOrRequestNotifyEvent event) {
        GroupInvite request = event.invite();

        Integer count = inviteRepo.generateOutboxNotificationsForNewBidirectionalGroupInvite(request.getInviteId());

        log.debug("Generated {} notification outbox entries to user {}, for invite ID: {}",
                count, request.getRecipient().getUserId(), request.getInviteId());
    }

    @Async
    @EventListener
    @Transactional
    public void handleNewGroupMemberNotify(NewGroupMemberNotifyEvent event) {
        NotificationType noteType = NotificationType.NEW_GROUP_MEMBER;

        Integer count = inviteRepo.generateOutboxNotificationsForNewGroupMember(
                event.notificationRecipient().getUserId(),
                event.newMember().getGroupListing().getGroupId(),
                event.newMember().getUser().getUserId(),
                event.newMember().getUser().getUsername(),
                event.invite().getInviteId(),
                noteType.toString());

        log.debug("Generated {} notification outbox entries to user {}, for new member: {}",
                count, event.notificationRecipient().getUserId(), event.newMember().getGroupMemberId());
    }

    @Async
    @EventListener
    @Transactional
    public void handleGroupMemberLeftNotify(GroupMemberLeftNotifyEvent event) {
        NotificationType noteType = NotificationType.GROUP_MEMBER_LEFT;

        Integer count = memberRepo.generateOutboxNotesForGroupMemberLeft(
                noteType.toString(),
                event.formerMember().getUser().getUserId(),
                event.formerMember().getUser().getUsername(),
                event.formerMember().getMemberStatus().toString(),
                event.listing().getUsers().getUserId(),
                event.listing().getGroupId(),
                event.listing().getListingTitle());

        log.debug("Generated {} notification outbox entries to user {}, for member leaving group: {}",
                count, event.listing().getUsers().getUserId(), event.listing().getGroupId());
    }

    @Async
    @EventListener
    @Transactional
    public void handleGroupMemberRemovedNotify(RemovedFromGroupNotifyEvent event) {
        NotificationType noteType = NotificationType.REMOVED_FROM_GROUP;

        // these args are all Longs, careful adjusting.
        Integer notesCount = memberRepo.generateOutboxNotesForRemovedFromGroupNotifyEvent(
                noteType.toString(),
                event.removedMember().getUserId(),
                event.removedFromListing().getListingTitle(),
                event.removedFromListing().getGroupId(),
                event.removedFromListing().getUsers().getUserId(),
                Instant.now().toString()
        );

        log.info("Removed from group notify event generated {} notification outbox entries to user {}, " +
                "regarding group: {}", notesCount, event.removedMember().getUsername(), event.removedFromListing().getGroupId());
    }
}
