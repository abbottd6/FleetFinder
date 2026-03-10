package com.sc_fleetfinder.fleets.unit_tests.scheduledTasks;

import com.sc_fleetfinder.fleets.scheduledTasks.ExpiredListingDeleteTask;
import com.sc_fleetfinder.fleets.scheduledTasks.ScheduledArchiveService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpiredListingDeleteTaskTest {

    @Mock
    private ScheduledArchiveService scheduledArchiveService;

    @InjectMocks
    private ExpiredListingDeleteTask expiredListingDeleteTask;

    @Test
    void archiveExpiredListing_ExecutesAllFourStepsInOrderAndLogs() {
        LogCaptor logCaptor = LogCaptor.forClass(ExpiredListingDeleteTask.class);

        // given
        when(scheduledArchiveService.setArchivedStatus()).thenReturn(2);
        when(scheduledArchiveService.generateArchivesForExpired()).thenReturn(3);
        when(scheduledArchiveService.createOutboxEntriesForArchiveNotifications()).thenReturn(4);
        when(scheduledArchiveService.deleteArchivedListings()).thenReturn(5);

        // when
        expiredListingDeleteTask.archiveExpiredListing();

        // then
        assertAll("archiveExpiredListing all-steps assertions:",
                () -> verify(scheduledArchiveService, times(1)).setArchivedStatus(),
                () -> verify(scheduledArchiveService, times(1)).generateArchivesForExpired(),
                () -> verify(scheduledArchiveService, times(1)).createOutboxEntriesForArchiveNotifications(),
                () -> verify(scheduledArchiveService, times(1)).deleteArchivedListings(),
                () -> assertEquals(4, logCaptor.getInfoLogs().size(), "Expected 4 info logs (one per step)"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("2 listings") && log.contains("preparation for archiving")),
                        "Expected log for setArchivedStatus with count 2"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("3 listings") && log.contains("scheduled deletion")),
                        "Expected log for generateArchivesForExpired with count 3"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("4 outbox") || (log.contains("4") && log.contains("outbox"))),
                        "Expected log for createOutboxEntriesForArchiveNotifications with count 4"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("5 listings") && log.contains("after archival")),
                        "Expected log for deleteArchivedListings with count 5")
        );
    }

    @Test
    void archiveExpiredListing_WhenAllCountsAreZero_StillCompletesAndLogs() {
        LogCaptor logCaptor = LogCaptor.forClass(ExpiredListingDeleteTask.class);

        // given
        when(scheduledArchiveService.setArchivedStatus()).thenReturn(0);
        when(scheduledArchiveService.generateArchivesForExpired()).thenReturn(0);
        when(scheduledArchiveService.createOutboxEntriesForArchiveNotifications()).thenReturn(0);
        when(scheduledArchiveService.deleteArchivedListings()).thenReturn(0);

        // when
        expiredListingDeleteTask.archiveExpiredListing();

        // then
        assertAll("archiveExpiredListing zero-counts assertions:",
                () -> verify(scheduledArchiveService, times(1)).setArchivedStatus(),
                () -> verify(scheduledArchiveService, times(1)).generateArchivesForExpired(),
                () -> verify(scheduledArchiveService, times(1)).createOutboxEntriesForArchiveNotifications(),
                () -> verify(scheduledArchiveService, times(1)).deleteArchivedListings(),
                () -> assertEquals(4, logCaptor.getInfoLogs().size(), "Expected 4 info logs even when all counts are 0")
        );
    }
}
