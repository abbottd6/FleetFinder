package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSenderTaskTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private OutboxTaskService outboxService;

    @InjectMocks
    private OutboxSenderTask outboxSenderTask;

    private NotificationOutbox buildMockOutbox(long id) {
        NotificationOutbox outbox = mock(NotificationOutbox.class);
        when(outbox.getOutboxId()).thenReturn(id);
        return outbox;
    }

    @Test
    void sendOutboxNotifications_WhenBatchIsEmpty_ReturnsEarlyWithoutSending() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);

        // given
        when(outboxService.claimPendingBatch(100)).thenReturn(0);
        when(outboxService.findStatus_Claimed(100)).thenReturn(Collections.emptyList());

        // when
        outboxSenderTask.sendOutboxNotifications();

        // then
        assertAll("empty batch — early return assertions:",
                () -> verify(notificationService, never()).prepareAndSendOutboxNotification(any()),
                () -> verify(outboxService, never()).markSent(anyLong()),
                () -> verify(outboxService, never()).markFailed(anyLong(), any()),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("claimed 0")),
                        "Expected log containing 'claimed 0'"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .noneMatch(log -> log.contains("Sent:")),
                        "Expected no 'Sent:' log when returning early")
        );
    }

    @Test
    void sendOutboxNotifications_WhenAllItemsSucceed_MarksEachSentAndLogsCount() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);

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
                () -> verify(outboxService, never()).markFailed(anyLong(), any()),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Sent: 2")),
                        "Expected log containing 'Sent: 2'"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Failed: 0")),
                        "Expected log containing 'Failed: 0'")
        );
    }

    @Test
    void sendOutboxNotifications_WhenSomeItemsFail_MarksFailedAndContinuesProcessing() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxSenderTask.class);

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
                () -> verify(outboxService, times(1)).markFailed(eq(2L), eq("db error")),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Sent: 2")),
                        "Expected log containing 'Sent: 2'"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Failed: 1")),
                        "Expected log containing 'Failed: 1'")
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
        verify(outboxService).markFailed(eq(10L), captor.capture());
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
        verify(outboxService).markFailed(anyLong(), isNull());
    }
}
