package com.sc_fleetfinder.fleets.events.AsyncListeners;

import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.events.NewMessageExternalNotifyEvent;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewMessageExternalNotifyListener {

    private final ParticipantRepository participantRepo;
    private final MessageRepository messageRepo;
    private final NotificationOutboxRepository obRepo;

    @Async
    @EventListener
    @Transactional
    public void handleNewMessageExternalNotify(NewMessageExternalNotifyEvent event) {
        Users recipient = event.recipient();
        Conversation conv = event.message().getConversation();

        Participant participantProfile = participantRepo.findByConversationAndUser(
                conv.getConversationId(), recipient.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + recipient.getUserId()
                        + " not found for Conversation with id " + conv.getConversationId()));

        if(participantProfile.isMuting()) {
            return;
        }

        if(participantProfile.isArchived()) {
            participantProfile.setArchived(false);
        }

        int outboxNotesGenerated = messageRepo.generateExternalDeliveryOutboxNotifications(
                recipient.getUserId(), event.message().getMsgId());

        log.debug("Generated {} notification outbox entries for user, '{}' for message ID: {}",
                outboxNotesGenerated, recipient.getUsername(), event.message().getMsgId());
    }
}
