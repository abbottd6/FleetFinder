package com.sc_fleetfinder.fleets.unit_tests.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.scheduledTasks.ListingVisStatusMaintenanceTask;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingVisStatusMaintenanceTaskTest {

    @Mock
    private GroupListingRepository listingRepo;

    @InjectMocks
    private ListingVisStatusMaintenanceTask listingVisStatusMaintenanceTask;

    @Test
    void updateListingVisStatuses_CreateOutboxEntriesAndUpdatesStatuses_AndLogs() {
        LogCaptor logCaptor = LogCaptor.forClass(ListingVisStatusMaintenanceTask.class);

        // given
        when(listingRepo.createNotificationOutboxEntriesForStatusUpdates()).thenReturn(5);
        when(listingRepo.updateListingVisStatuses()).thenReturn(3);

        // when
        listingVisStatusMaintenanceTask.updateListingVisStatuses();

        // then
        assertAll("updateListingVisStatuses assertions:",
                () -> verify(listingRepo, times(1)).createNotificationOutboxEntriesForStatusUpdates(),
                () -> verify(listingRepo, times(1)).updateListingVisStatuses(),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("5") && log.contains("outbox")),
                        "Expected info log mentioning 5 outbox entries"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("3") && log.contains("vis_status")),
                        "Expected info log mentioning 3 statuses updated")
        );
    }

    @Test
    void updateListingVisStatuses_WhenCountsAreZero_StillCompletesAndLogs() {
        LogCaptor logCaptor = LogCaptor.forClass(ListingVisStatusMaintenanceTask.class);

        // given
        when(listingRepo.createNotificationOutboxEntriesForStatusUpdates()).thenReturn(0);
        when(listingRepo.updateListingVisStatuses()).thenReturn(0);

        // when
        listingVisStatusMaintenanceTask.updateListingVisStatuses();

        // then
        assertAll("updateListingVisStatuses zero-counts assertions:",
                () -> verify(listingRepo, times(1)).createNotificationOutboxEntriesForStatusUpdates(),
                () -> verify(listingRepo, times(1)).updateListingVisStatuses(),
                () -> assertEquals(2, logCaptor.getInfoLogs().size(), "Expected 2 info logs even when counts are 0")
        );
    }
}
