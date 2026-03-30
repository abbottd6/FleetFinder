package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.config.WebSocketSessionTracker;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
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

    @Mock
    private WebSocketSessionTracker wsSessionTracker;

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

    // PUSH channel variant of the external outbox helper
    private NotificationOutbox buildMockPushOutbox(long id, String kcId, int attemptCount) {
        NotificationOutbox outbox = mock(NotificationOutbox.class);
        when(outbox.getOutboxId()).thenReturn(id);
        when(outbox.getDeliveryChannel()).thenReturn(DeliveryChannel.PUSH);
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
        when(outboxService.claimPendingBatch(200)).thenReturn(0);
        when(outboxService.findStatus_Claimed(200)).thenReturn(Collections.emptyList());

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

        when(outboxService.claimPendingBatch(200)).thenReturn(2);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox1, outbox2));
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

        when(outboxService.claimPendingBatch(200)).thenReturn(3);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox1, outbox2, outbox3));
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

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
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

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        doThrow(new RuntimeException((String) null)).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        verify(outboxService).incrementAndCheckFailureCounter(same(outbox), isNull());
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_UserRecentlyActive_AttemptCountLow_DelaysNotification() {
        // given — external channel, user WS-connected and recently active, attemptCount < 10 → delay
        NotificationOutbox outbox = buildMockExternalOutbox(5L, "kc-1", 2);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-1")).thenReturn(true);
        when(activityCache.hasRecentAccess("kc-1", 300)).thenReturn(true);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("delay when user is WS-connected, recently active, and attemptCount < 10:",
                () -> verify(wsSessionTracker, times(1)).isUserConnected("kc-1"),
                () -> verify(outboxService, times(1)).markForUserActive_Delayed(5L),
                () -> verify(notificationService, never()).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, never()).markSent(anyLong()),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(), any())
        );
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_UserRecentlyActive_AttemptCountMaxedOut_ProceedsToSend() {
        // given — user WS-connected and recently active, but attemptCount >= 10 bypasses the delay
        NotificationOutbox outbox = buildMockExternalOutbox(5L, "kc-1", 10);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-1")).thenReturn(true);
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
    void sendOutboxNotifications_ExternalChannel_UserConnected_NotRecentlyActive_ProceedsToSend() {
        // given — user is WS-connected but not recently active; activity check passes → no delay
        NotificationOutbox outbox = buildMockExternalOutbox(6L, "kc-2", 1);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-2")).thenReturn(true);
        when(activityCache.hasRecentAccess("kc-2", 300)).thenReturn(false);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("no activity delay when user is WS-connected but not recently active:",
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(6L)
        );
    }

    @Test
    void sendOutboxNotifications_InAppChannel_UserRecentlyActive_SkipsActivityCheckAndSends() {
        // given — IN_APP channel: the activity guard block is never entered
        NotificationOutbox outbox = buildMockOutbox(8L); // IN_APP delivery channel

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("IN_APP channel: WS tracker and activity cache never consulted:",
                () -> verify(wsSessionTracker, never()).isUserConnected(any()),
                () -> verify(activityCache, never()).hasRecentAccess(any(), anyInt()),
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(8L)
        );
    }

    // ─── NullPointerException / ResourceNotFoundException branch ─────────────

    @Test
    void sendOutboxNotifications_NullPointerException_CallsMarkFailedDirectly_NotIncrementAndCheck() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given — NPE hits the NullPointerException | ResourceNotFoundException catch block → markFailed directly
        NotificationOutbox outbox = buildMockOutbox(11L);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        doThrow(new NullPointerException("npe error")).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("NPE → markFailed directly:",
                () -> verify(outboxService).markFailed(11L, "npe error"),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(NotificationOutbox.class), any()),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Failed: 1")),
                        "NPE should increment failedCount — 'Failed: 1' expected in log")
        );
    }

    @Test
    void sendOutboxNotifications_ResourceNotFoundException_CallsMarkFailedDirectly_NotIncrementAndCheck() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given — ResourceNotFoundException hits the same terminal catch block
        NotificationOutbox outbox = buildMockOutbox(12L);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        doThrow(new ResourceNotFoundException("Resource gone")).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("ResourceNotFoundException → markFailed directly:",
                () -> verify(outboxService).markFailed(12L, "Resource gone"),
                () -> verify(outboxService, never()).incrementAndCheckFailureCounter(any(NotificationOutbox.class), any()),
                () -> assertTrue(logCaptor.getDebugLogs().stream()
                        .anyMatch(log -> log.contains("Failed: 1")),
                        "'Failed: 1' expected in log for ResourceNotFoundException")
        );
    }

    @Test
    void sendOutboxNotifications_NullPointerException_LongMessage_TruncatesTo900() {
        // given — NPE with message > 900 chars → truncated before passing to markFailed
        NotificationOutbox outbox = buildMockOutbox(13L);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        doThrow(new NullPointerException("x".repeat(1000))).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(outboxService).markFailed(eq(13L), captor.capture());
        String captured = captor.getValue();

        assertAll("NPE long message truncation:",
                () -> assertEquals(900, captured.length(), "Error message should be truncated to 900 chars"),
                () -> assertEquals("x".repeat(900), captured)
        );
    }

    @Test
    void sendOutboxNotifications_NullPointerException_NullMessage_PassesNullToMarkFailed() {
        // given — NPE with null message → null passed through to markFailed
        NotificationOutbox outbox = buildMockOutbox(14L);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        doThrow(new NullPointerException((String) null)).when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        verify(outboxService).markFailed(eq(14L), isNull());
    }

    // ─── Activity-delay edge cases ────────────────────────────────────────────

    @Test
    void sendOutboxNotifications_PushChannel_UserRecentlyActive_AttemptCountLow_Delays() {
        // given — PUSH channel (not just DISCORD) also triggers the WS + activity-delay check
        NotificationOutbox outbox = buildMockPushOutbox(15L, "kc-push", 1);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-push")).thenReturn(true);
        when(activityCache.hasRecentAccess("kc-push", 300)).thenReturn(true);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("PUSH channel: activity delay applies just like DISCORD:",
                () -> verify(wsSessionTracker, times(1)).isUserConnected("kc-push"),
                () -> verify(outboxService, times(1)).markForUserActive_Delayed(15L),
                () -> verify(notificationService, never()).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, never()).markSent(anyLong())
        );
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_AttemptCount9_UserActive_Delays() {
        // given — user WS-connected and active; attemptCount=9 is still < 10 (boundary: delay still applies)
        NotificationOutbox outbox = buildMockExternalOutbox(16L, "kc-3", 9);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-3")).thenReturn(true);
        when(activityCache.hasRecentAccess("kc-3", 300)).thenReturn(true);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("attemptCount=9 (boundary: still < 10) → delay still applied:",
                () -> verify(wsSessionTracker, times(1)).isUserConnected("kc-3"),
                () -> verify(outboxService, times(1)).markForUserActive_Delayed(16L),
                () -> verify(notificationService, never()).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, never()).markSent(anyLong())
        );
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_PreviouslyDeferred_UserNowInactive_Sends() {
        // given — outbox was deferred several times (attemptCount=6) while user was active;
        //         user is still WS-connected but no longer recently active → notification proceeds
        NotificationOutbox outbox = buildMockExternalOutbox(17L, "kc-4", 6);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-4")).thenReturn(true);
        when(activityCache.hasRecentAccess("kc-4", 300)).thenReturn(false);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("previously deferred notification sends once user is inactive:",
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(17L)
        );
    }

    @Test
    void sendOutboxNotifications_ExternalChannel_UserNotConnectedToWs_SkipsActivityCheckAndSendsImmediately() {
        // given — user has no active WS session; the inner activity check is never reached → sends immediately
        NotificationOutbox outbox = buildMockExternalOutbox(20L, "kc-offline", 1);

        when(outboxService.claimPendingBatch(200)).thenReturn(1);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox));
        when(wsSessionTracker.isUserConnected("kc-offline")).thenReturn(false);
        doNothing().when(notificationService).prepareAndSendOutboxNotification(outbox);

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("user not WS-connected: activity cache skipped, sends immediately:",
                () -> verify(wsSessionTracker, times(1)).isUserConnected("kc-offline"),
                () -> verify(activityCache, never()).hasRecentAccess(any(), anyInt()),
                () -> verify(outboxService, never()).markForUserActive_Delayed(anyLong()),
                () -> verify(notificationService, times(1)).prepareAndSendOutboxNotification(outbox),
                () -> verify(outboxService, times(1)).markSent(20L)
        );
    }

    @Test
    void sendOutboxNotifications_SkipException_MarksSkippedAndContinuesProcessing() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);
        logCaptor.setLogLevelToDebug();

        // given — outbox1 throws skip exception, outbox2 succeeds
        NotificationOutbox outbox1 = buildMockOutbox(1L);
        NotificationOutbox outbox2 = buildMockOutbox(2L);

        when(outboxService.claimPendingBatch(200)).thenReturn(2);
        when(outboxService.findStatus_Claimed(200)).thenReturn(List.of(outbox1, outbox2));
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
