package com.sc_fleetfinder.fleets.scheduledTasks;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxCleanupTaskTest {

    @Mock
    private OutboxCleanupService outboxCleanupService;

    @InjectMocks
    private OutboxCleanupTask task;

    @Test
    void cleanNotificationOutbox_AllStepsSucceed_CallsAllThreeMethods() {
        when(outboxCleanupService.deleteExpiredArchives()).thenReturn(2);
        when(outboxCleanupService.archiveOldNotificationOutboxEntries()).thenReturn(5);
        when(outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries()).thenReturn(3);

        task.cleanNotificationOutbox();

        assertAll("cleanNotificationOutbox success path assertions:",
                () -> verify(outboxCleanupService).deleteExpiredArchives(),
                () -> verify(outboxCleanupService).archiveOldNotificationOutboxEntries(),
                () -> verify(outboxCleanupService).deleteRecentlyArchivedNotificationOutboxEntries()
        );
    }

    @Test
    void cleanNotificationOutbox_DeleteExpiredArchivesThrows_ContinuesToRemainingSteps() {
        when(outboxCleanupService.deleteExpiredArchives()).thenThrow(new RuntimeException("db error"));
        when(outboxCleanupService.archiveOldNotificationOutboxEntries()).thenReturn(4);
        when(outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries()).thenReturn(2);

        assertDoesNotThrow(() -> task.cleanNotificationOutbox());

        assertAll("cleanNotificationOutbox first-step-exception assertions:",
                () -> verify(outboxCleanupService).deleteExpiredArchives(),
                () -> verify(outboxCleanupService).archiveOldNotificationOutboxEntries(),
                () -> verify(outboxCleanupService).deleteRecentlyArchivedNotificationOutboxEntries()
        );
    }

    @Test
    void cleanNotificationOutbox_ArchiveOldEntriesThrows_ContinuesToDeleteStep() {
        when(outboxCleanupService.deleteExpiredArchives()).thenReturn(1);
        when(outboxCleanupService.archiveOldNotificationOutboxEntries()).thenThrow(new RuntimeException("archive error"));
        when(outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries()).thenReturn(0);

        assertDoesNotThrow(() -> task.cleanNotificationOutbox());

        assertAll("cleanNotificationOutbox second-step-exception assertions:",
                () -> verify(outboxCleanupService).archiveOldNotificationOutboxEntries(),
                () -> verify(outboxCleanupService).deleteRecentlyArchivedNotificationOutboxEntries()
        );
    }

    @Test
    void cleanNotificationOutbox_AllStepsThrow_DoesNotPropagateException() {
        when(outboxCleanupService.deleteExpiredArchives()).thenThrow(new RuntimeException("err1"));
        when(outboxCleanupService.archiveOldNotificationOutboxEntries()).thenThrow(new RuntimeException("err2"));
        when(outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries()).thenThrow(new RuntimeException("err3"));

        assertDoesNotThrow(() -> task.cleanNotificationOutbox());
    }

    @Test
    void cleanNotificationOutbox_WhenCountsAreNonZero_LogsInfoMessages() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxCleanupTask.class);
        when(outboxCleanupService.deleteExpiredArchives()).thenReturn(3);
        when(outboxCleanupService.archiveOldNotificationOutboxEntries()).thenReturn(7);
        when(outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries()).thenReturn(6);

        task.cleanNotificationOutbox();

        assertThat(logCaptor.getInfoLogs()).anyMatch(log -> log.contains("3") && log.contains("archives"));
    }

    @Test
    void cleanNotificationOutbox_WhenAllCountsAreZero_NoInfoLogged() {
        LogCaptor logCaptor = LogCaptor.forClass(OutboxCleanupTask.class);
        when(outboxCleanupService.deleteExpiredArchives()).thenReturn(0);
        when(outboxCleanupService.archiveOldNotificationOutboxEntries()).thenReturn(0);
        when(outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries()).thenReturn(0);

        task.cleanNotificationOutbox();

        assertThat(logCaptor.getInfoLogs()).isEmpty();
    }
}
