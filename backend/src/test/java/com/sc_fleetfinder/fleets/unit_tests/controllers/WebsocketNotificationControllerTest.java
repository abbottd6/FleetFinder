package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.controllers.WebsocketNotificationController;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ReceiveReadNotesDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebsocketNotificationControllerTest {

    @Mock private NotificationService notificationService;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private UserService userService;

    @InjectMocks
    private WebsocketNotificationController controller;

    private Users mockUser;
    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockPrincipal = () -> "mockKcId";
        when(userService.verifyUser("mockKcId")).thenReturn(mockUser);
    }

    // ─── updateRead ───────────────────────────────────────────────────────────

    @Test
    void updateRead_MarkedCountPositive_SendsUnreadCount() {
        ReceiveReadNotesDto readDto = new ReceiveReadNotesDto(List.of(1L, 2L));
        when(notificationService.updateRead(mockUser, readDto)).thenReturn(2);
        when(notificationService.countUnread(1L)).thenReturn(3);

        controller.updateRead(mockPrincipal, readDto);

        verify(notificationService).countUnread(1L);
        verify(messagingTemplate).convertAndSendToUser(
                eq("mockKcId"), eq("/queue/system.notify_count"), any());
    }

    @Test
    void updateRead_MarkedCountZero_DoesNotSendWsMessage() {
        ReceiveReadNotesDto readDto = new ReceiveReadNotesDto(List.of(1L));
        when(notificationService.updateRead(mockUser, readDto)).thenReturn(0);

        controller.updateRead(mockPrincipal, readDto);

        verify(notificationService, never()).countUnread(any());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    void updateRead_MarkedCountNull_DoesNotSendWsMessage() {
        ReceiveReadNotesDto readDto = new ReceiveReadNotesDto(List.of(1L));
        when(notificationService.updateRead(mockUser, readDto)).thenReturn(null);

        controller.updateRead(mockPrincipal, readDto);

        verify(notificationService, never()).countUnread(any());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    // ─── getUnreadCount ───────────────────────────────────────────────────────

    @Test
    void getUnreadCount_Success_SendsUnreadCount() {
        when(notificationService.countUnread(1L)).thenReturn(5);

        controller.getUnreadCount(mockPrincipal);

        verify(messagingTemplate).convertAndSendToUser(
                eq("mockKcId"), eq("/queue/system.notify_count"), any());
    }

    @Test
    void getUnreadCount_UserNotFound_LogsAndNoWsMessage() {
        when(userService.verifyUser("mockKcId")).thenThrow(new ResourceNotFoundException("User not found"));

        controller.getUnreadCount(mockPrincipal);

        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }
}
