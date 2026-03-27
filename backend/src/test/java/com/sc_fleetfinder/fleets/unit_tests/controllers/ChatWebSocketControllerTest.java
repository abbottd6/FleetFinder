package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.controllers.ChatWebSocketController;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadResponseDto;
import com.sc_fleetfinder.fleets.services.chat_services.ChatWsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock private ChatWsService chatWsService;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatWebSocketController controller;

    private Principal mockPrincipal;
    private UserUnreadResponseDto mockResponse;

    @BeforeEach
    void setUp() {
        mockPrincipal = () -> "test-kc-sub";
        mockResponse = new UserUnreadResponseDto(3L, Set.of());
    }

    // ─── markRead ─────────────────────────────────────────────────────────────

    @Test
    void markRead_CallsUpdateConversationLastRead_ThenUpdateUserUnread() {
        ChatReadDto dto = new ChatReadDto(42L, 99L);
        when(chatWsService.updateUserUnread("test-kc-sub")).thenReturn(mockResponse);

        controller.markRead(dto, mockPrincipal);

        InOrder order = inOrder(chatWsService);
        order.verify(chatWsService).updateConversationLastRead("test-kc-sub", dto);
        order.verify(chatWsService).updateUserUnread("test-kc-sub");
    }

    @Test
    void markRead_SendsUnreadResponseToUserQueue() {
        ChatReadDto dto = new ChatReadDto(42L, 99L);
        when(chatWsService.updateUserUnread("test-kc-sub")).thenReturn(mockResponse);

        controller.markRead(dto, mockPrincipal);

        verify(messagingTemplate).convertAndSendToUser(
                eq("test-kc-sub"), eq("/queue/chat.unread"), eq(mockResponse));
    }

    @Test
    void markRead_UsesSubFromPrincipal() {
        Principal otherPrincipal = () -> "other-sub-789";
        ChatReadDto dto = new ChatReadDto(1L, 1L);
        UserUnreadResponseDto otherResponse = new UserUnreadResponseDto(0L, Set.of());
        when(chatWsService.updateUserUnread("other-sub-789")).thenReturn(otherResponse);

        controller.markRead(dto, otherPrincipal);

        verify(chatWsService).updateConversationLastRead("other-sub-789", dto);
        verify(chatWsService).updateUserUnread("other-sub-789");
        verify(messagingTemplate).convertAndSendToUser(
                eq("other-sub-789"), eq("/queue/chat.unread"), eq(otherResponse));
    }

    // ─── getUnreadTotal ───────────────────────────────────────────────────────

    @Test
    void getUnreadTotal_CallsUpdateUserUnread_SendsResultToUser() {
        when(chatWsService.updateUserUnread("test-kc-sub")).thenReturn(mockResponse);

        controller.getUnreadTotal(mockPrincipal);

        verify(chatWsService).updateUserUnread("test-kc-sub");
        verify(messagingTemplate).convertAndSendToUser(
                eq("test-kc-sub"), eq("/queue/chat.unread"), eq(mockResponse));
    }

    @Test
    void getUnreadTotal_DoesNotCallUpdateConversationLastRead() {
        when(chatWsService.updateUserUnread("test-kc-sub")).thenReturn(mockResponse);

        controller.getUnreadTotal(mockPrincipal);

        verify(chatWsService, never()).updateConversationLastRead(any(), any());
    }
}
