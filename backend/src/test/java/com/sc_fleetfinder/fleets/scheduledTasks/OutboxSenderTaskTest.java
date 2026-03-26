package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.SkipExternalNotificationProcessingException;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OutboxSenderTaskTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private OutboxTaskService outboxService;

    @Mock
    private UserActivityCache activityCache;

    @InjectMocks
    private OutboxSenderTask outboxSenderTask;

    // Default: IN_APP delivery channel — bypasses the activity check
    private NotificationOutbox buildMockOutbox(long id) {
        NotificationOutbox outbox = mock(NotificationOutbox.class);
        when(outbox.getOutboxId()).thenReturn(id);
        when(outbox.getDeliveryChannel()).thenReturn(DeliveryChannel.IN_APP);
        return outbox;
    }

    // External channel outbox with a stubbed entity owner and attempt count
    private NotificationOutbox buildMockExternalOutbox(long id, String kcId, int attemptCount) {
        NotificationOutbox outbox = mock(NotificationOutbox.class);
        when(outbox.getOutboxId()).thenReturn(id);
        when(outbox.getDeliveryChannel()).thenReturn(DeliveryChannel.DISCORD);
        Users owner = mock(Users.class);
        when(owner.getKeycloakId()).thenReturn(kcId);
        when(outbox.getEntityOwner()).thenReturn(owner);
        when(outbox.getAttemptCount()).thenReturn(attemptCount);
        return outbox;
    }

    @Test
    void sendOutboxNotifications_WhenBatchIsEmpty_ReturnsEarlyWithoutSending() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given
        when(outboxService.claimPendingBatch(100)).thenReturn(0);
        when(outboxService.findStatus_Claimed(100)).thenReturn(Collections.emptyList());

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("empty batch — early return assertions:",
                () -> verify(notificationService, never()).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, never()).markSent(anyLong()),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(NotificationOutbox.class), any()),
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(outboxService, never()).markSkipped(anyLong(), any()),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("claimed 0")),
                        "Expected debug log containing 'claimed 0'"),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .noneMatch(log -> log.contains("Sent:")),
                        "Expected no 'Sent:' log when returning early")
        );
    }

    @Test
    void sendOutboxNotifications_WhenAllItemsSucceed_MarksEachSentAndLogsCount() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given
        NotificationOutbox outbox1 = buildMockOutbox(1L);
        NotificationOutbox outbox2 = buildMockOutbox(2L);

        when(outboxService.claimPendingBatch(100)).thenReturn(2);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox1, outbox2));
        doNothing().when(notificationService).prepareAndSendOutboxNotification(any());

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("all-success assertions:",
                () -> verify(notificationService, times(2)).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, times(1)).markSent(1L),
                () -> verify(outboxService, times(1)).markSent(2L),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(NotificationOutbox.class), any()),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Sent: 2")),
                        "Expected debug log containing 'Sent: 2'"),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Failed: 0")),
                        "Expected debug log containing 'Failed: 0'")
        );
    }

    @Test
    void sendOutboxNotifications_WhenSomeItemsFail_MarksFailedAndContinuesProcessing() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given
        NotificationOutbox outbox1 = buildMockOutbox(1L);
        NotificationOutbox outbox2 = buildMockOutbox(2L);
        NotificationOutbox outbox3 = buildMockOutbox(3L);

        when(outboxService.claimPendingBatch(100)).thenReturn(3);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox1, outbox2, outbox3));
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox1);
        doThrow(new RuntimeException("db error")).when(notificationService).prepareAndSendOutboxNotification(outbox2);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox3);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("partial failure assertions:",
                () -> verify(outboxService, times(2)).markSent(anyLong()),
                () -> verify(outboxService, times(1)).incrementAndCheckFailureCounter(same(outbox2), eq("db error")),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Sent: 2")),
                        "Expected debug log containing 'Sent: 2'"),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Failed: 1")),
                        "Expected debug log containing 'Failed: 1'")
        );
    }

    @Test
    void sendOutboxNotifications_WhenErrorMessageExceedsLimit_TruncatesTo900Chars() {
        // given
        NotificationOutbox outbox = buildMockOutbox(10L);

        when(outboxService.claimPendingBatch(100)).thenReturn(1);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox));
        doThrow(new RuntimeException("x".repeat(1000))).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(outboxService).incrementAndCheckFailureCounter(same(outbox), captor.capture());
        String captured = captor.getValue();

        assertAll("truncation assertions:",
                () -> assertEquals(900, captured.length(), "Error message should be truncated to 900 chars"),
                () -> assertEquals("x".repeat(900), captured, "Truncated message should be 900 'x' chars")
        );
    }

    @Test
    void sendOutboxNotifications_WhenErrorMessageIsNull_PassesNullToMarkFailed() {
        // given
        NotificationOutbox outbox = buildMockOutbox(7L);

        when(outboxService.claimPendingBatch(100)).thenReturn(1);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox));
        doThrow(new RuntimeException((String) null)).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        verify(outboxService).incrementAndCheckFailureCounter(same(outbox), isNull());
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_UserRecentlyActive_AttemptCountLow_DelaysNotification() {
        // given — external channel, user recently active, attemptCount < 10 → delay
        NotificationOutbox outbox = buildMockExternalOutbox(5L, "kc-1", 2);

        when(outboxService.claimPendingBatch(100)).thenReturn(1);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox));
        when(activityCache.hasRecentAccess("kc-1", 300)).thenReturn(true);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("delay when user is recently active and attemptCount < 10:",
                () -> verify(outboxService, times(1)).markForUserActive_Delayed(5L),
                () -> verify(notificationService, never()).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, never()).markSent(anyLong()),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(), any())
        );
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_UserRecentlyActive_AttemptCountMaxedOut_ProceedsToSend() {
        // given — attemptCount >= 10 bypasses the activity delay even when user is active
        NotificationOutbox outbox = buildMockExternalOutbox(5L, "kc-1", 10);

        when(outboxService.claimPendingBatch(100)).thenReturn(1);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox));
        when(activityCache.hasRecentAccess("kc-1", 300)).thenReturn(true);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("maxed-out attempt count bypasses activity delay:",
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(5L)
        );
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_UserNotRecentlyActive_ProceedsToSend() {
        // given — user is not recently active, so no delay
        NotificationOutbox outbox = buildMockExternalOutbox(6L, "kc-2", 1);

        when(outboxService.claimPendingBatch(100)).thenReturn(1);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox));
        when(activityCache.hasRecentAccess("kc-2", 300)).thenReturn(false);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("no activity delay when user is not recently active:",
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(6L)
        );
    }

    @Test
    void sendOutboxNotifications_InAppChannel_UserRecentlyActive_SkipsActivityCheckAndSends() {
        // given — IN_APP channel: the activity guard block is never entered
        NotificationOutbox outbox = buildMockOutbox(8L); // IN_APP delivery channel

        when(outboxService.claimPendingBatch(100)).thenReturn(1);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox));
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("IN_APP channel: activity cache never consulted:",
                () -> verify(activityCache, never()).hasRecentAccess(any(), anyInt()),
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(8L)
        );
    }

    @Test
    void sendOutboxNotifications_SkipException_MarksSkippedAndContinuesProcessing() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given — outbox1 throws skip exception, outbox2 succeeds
        NotificationOutbox outbox1 = buildMockOutbox(1L);
        NotificationOutbox outbox2 = buildMockOutbox(2L);

        when(outboxService.claimPendingBatch(100)).thenReturn(2);
        when(outboxService.findStatus_Claimed(100)).thenReturn(List.of(outbox1, outbox2));
        doThrow(new SkipExternalNotificationProcessingException("already read"))
                .when(notificationService).prepareAndSendOutboxNotification(outbox1);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox2);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("skip exception handling:",
                () -> verify(outboxService, times(1)).markSkipped(1L, "already read"),
                () -> verify(outboxService, times(1)).markSent(2L),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(), any()),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Skipped: 1")),
                        "Expected debug log containing 'Skipped: 1'"),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Sent: 1")),
                        "Expected debug log containing 'Sent: 1'")
        );
    }
}
