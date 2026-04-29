package com.sc_fleetfinder.fleets.events.AsyncListeners.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupInviteRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupMemberRepository;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.events.GroupManagement.GroupMemberLeftNotifyEvent;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewGroupMemberNotifyEvent;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewGroupInviteOrRequestNotifyEvent;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupMembershipEventListener {

    private final GroupInviteRepository inviteRepo;
    private final GroupMemberRepository memberRepo;

    @Async
    @EventListener
    @Transactional
    public void handleNewInviteRequestNotify(NewGroupInviteOrRequestNotifyEvent event) {
        GroupInvite request = event.invite();

        Integer count = inviteRepo.generateOutboxNotificationsForNewBidirectionalGroupInvite(request.getInviteId());

        log.info("Generated {} notification outbox entries to user {}, for invite ID: {}",
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

        log.info("Generated {} notification outbox entries to user {}, for new member: {}",
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

        log.info("Generated {} notification outbox entries to user {}, for member leaving group: {}",
                count, event.listing().getUsers().getUserId(), event.listing().getGroupId());
    }
}
