package com.sc_fleetfinder.fleets.unit_tests.events;

import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.events.AsyncListeners.NewMessageExternalNotifyListener;
import com.sc_fleetfinder.fleets.events.NewMessageExternalNotifyEvent;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewMessageExternalNotifyListenerTest {

    @Mock
    private ParticipantRepository participantRepo;

    @Mock
    private MessageRepository messageRepo;

    @InjectMocks
    private NewMessageExternalNotifyListener listener;

    private Users recipient;
    private Conversation conversation;
    private Message message;
    private NewMessageExternalNotifyEvent event;

    @BeforeEach
    void setUp() {
        recipient = new Users();
        recipient.setUserId(10L);
        recipient.setUsername("recipientUser");

        conversation = new Conversation();
        conversation.setConversationId(5L);

        message = new Message();
        message.setMsgId(100L);
        message.setConversation(conversation);

        event = new NewMessageExternalNotifyEvent(recipient, message);
    }

    @Test
    void handleNewMessageExternalNotify_WhenNotMuting_GeneratesOutboxNotifications() {
        Participant participant = new Participant();
        participant.setMuting(false);
        when(participantRepo.findByConversationAndUser(5L, 10L)).thenReturn(Optional.of(participant));
        when(messageRepo.generateExternalDeliveryOutboxNotifications(10L, 100L)).thenReturn(2);

        listener.handleNewMessageExternalNotify(event);

        assertAll("non-muting path assertions:",
                () -> verify(participantRepo).findByConversationAndUser(5L, 10L),
                () -> verify(messageRepo).generateExternalDeliveryOutboxNotifications(10L, 100L)
        );
    }

    @Test
    void handleNewMessageExternalNotify_WhenMuting_SkipsOutboxGeneration() {
        Participant participant = new Participant();
        participant.setMuting(true);
        when(participantRepo.findByConversationAndUser(5L, 10L)).thenReturn(Optional.of(participant));

        listener.handleNewMessageExternalNotify(event);

        assertAll("muting path assertions:",
                () -> verify(participantRepo).findByConversationAndUser(5L, 10L),
                () -> verify(messageRepo, never()).generateExternalDeliveryOutboxNotifications(anyLong(), anyLong())
        );
    }

    @Test
    void handleNewMessageExternalNotify_WhenParticipantNotFound_ThrowsResourceNotFoundException() {
        when(participantRepo.findByConversationAndUser(5L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listener.handleNewMessageExternalNotify(event))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(messageRepo, never()).generateExternalDeliveryOutboxNotifications(anyLong(), anyLong());
    }
}
