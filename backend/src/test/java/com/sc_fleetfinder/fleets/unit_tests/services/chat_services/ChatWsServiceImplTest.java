package com.sc_fleetfinder.fleets.unit_tests.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.chat_services.ChatService;
import com.sc_fleetfinder.fleets.services.chat_services.ChatWsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatWsServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ParticipantRepository participantRepo;

    @Mock
    private MessageRepository messageRepo;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatWsServiceImpl chatWsService;

    private Users user;
    private ChatReadDto readDto;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId(1L);
        user.setKeycloakId("test-kc-id");

        readDto = new ChatReadDto(5L, 20L); // conversationId=5, lastReadMsgId=20
    }

    // ─── updateConversationLastRead ────────────────────────────────────────────

    @Test
    void updateConversationLastRead_WhenNoPriorLastRead_SetsNewLastRead() {
        Message incomingRead = new Message();
        incomingRead.setMsgId(20L);

        Participant part = new Participant();
        part.setLastReadMessage(null);

        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(messageRepo.findMessageByConversationIdAndMessageId(5L, 20L)).thenReturn(Optional.of(incomingRead));
        when(participantRepo.findByConversationAndUser(5L, 1L)).thenReturn(Optional.of(part));

        chatWsService.updateConversationLastRead("test-kc-id", readDto);

        assertAll("no prior last read — should update:",
                () -> assertThat(part.getLastReadMessage()).isEqualTo(incomingRead),
                () -> verify(participantRepo).save(part)
        );
    }

    @Test
    void updateConversationLastRead_WithHigherMsgId_UpdatesLastRead() {
        Message existingRead = new Message();
        existingRead.setMsgId(10L);

        Message incomingRead = new Message();
        incomingRead.setMsgId(20L);

        Participant part = new Participant();
        part.setLastReadMessage(existingRead);

        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(messageRepo.findMessageByConversationIdAndMessageId(5L, 20L)).thenReturn(Optional.of(incomingRead));
        when(participantRepo.findByConversationAndUser(5L, 1L)).thenReturn(Optional.of(part));

        chatWsService.updateConversationLastRead("test-kc-id", readDto);

        assertAll("higher msgId — should update:",
                () -> assertThat(part.getLastReadMessage()).isEqualTo(incomingRead),
                () -> verify(participantRepo).save(part)
        );
    }

    @Test
    void updateConversationLastRead_WithSameMsgId_SkipsUpdate() {
        Message existingRead = new Message();
        existingRead.setMsgId(20L);

        Participant part = new Participant();
        part.setLastReadMessage(existingRead);

        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(messageRepo.findMessageByConversationIdAndMessageId(5L, 20L)).thenReturn(Optional.of(existingRead));
        when(participantRepo.findByConversationAndUser(5L, 1L)).thenReturn(Optional.of(part));

        chatWsService.updateConversationLastRead("test-kc-id", readDto);

        verify(participantRepo, never()).save(any());
    }

    @Test
    void updateConversationLastRead_WithLowerMsgId_SkipsUpdate() {
        // Existing read is at msg 30; incoming is at msg 20 — should not regress
        Message existingRead = new Message();
        existingRead.setMsgId(30L);

        Message olderRead = new Message();
        olderRead.setMsgId(20L);

        Participant part = new Participant();
        part.setLastReadMessage(existingRead);

        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(messageRepo.findMessageByConversationIdAndMessageId(5L, 20L)).thenReturn(Optional.of(olderRead));
        when(participantRepo.findByConversationAndUser(5L, 1L)).thenReturn(Optional.of(part));

        chatWsService.updateConversationLastRead("test-kc-id", readDto);

        verify(participantRepo, never()).save(any());
    }

    @Test
    void updateConversationLastRead_WhenUserNotFound_ThrowsResourceNotFoundException() {
        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatWsService.updateConversationLastRead("test-kc-id", readDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateConversationLastRead_WhenMessageNotFound_ThrowsResourceNotFoundException() {
        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(messageRepo.findMessageByConversationIdAndMessageId(5L, 20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatWsService.updateConversationLastRead("test-kc-id", readDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateConversationLastRead_WhenParticipantNotFound_ThrowsResourceNotFoundException() {
        Message lastRead = new Message();
        lastRead.setMsgId(20L);

        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(messageRepo.findMessageByConversationIdAndMessageId(5L, 20L)).thenReturn(Optional.of(lastRead));
        when(participantRepo.findByConversationAndUser(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatWsService.updateConversationLastRead("test-kc-id", readDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── updateUserUnread ──────────────────────────────────────────────────────

    @Test
    void updateUserUnread_DelegatesToChatServiceWithCorrectUserId() {
        UserUnreadResponseDto expectedDto = new UserUnreadResponseDto(3L, Set.of());
        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.of(user));
        when(chatService.getUserUnreadCounts(1L)).thenReturn(expectedDto);

        UserUnreadResponseDto result = chatWsService.updateUserUnread("test-kc-id");

        assertAll("updateUserUnread delegation:",
                () -> assertThat(result).isEqualTo(expectedDto),
                () -> verify(chatService).getUserUnreadCounts(1L)
        );
    }

    @Test
    void updateUserUnread_WhenUserNotFound_ThrowsResourceNotFoundException() {
        when(userRepo.findByKeycloakId("test-kc-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatWsService.updateUserUnread("test-kc-id"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
