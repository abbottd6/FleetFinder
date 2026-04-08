package com.sc_fleetfinder.fleets.events.AsyncListeners.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupInviteRepository;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.events.GroupManagement.NewInviteRequestEvent;
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

    @Async
    @EventListener
    @Transactional
    public void handleNewInviteRequestNotify(NewInviteRequestEvent event) {
        GroupInvite request = event.invite();

        inviteRepo.generateOutboxNotificationsForNewGroupInviteRequest(request.getInviteId());

        log.info("Generated a notification outbox entry to user {}, for invite ID: {}",
                request.getRecipient().getUserId(), request.getInviteId());
    }
}
