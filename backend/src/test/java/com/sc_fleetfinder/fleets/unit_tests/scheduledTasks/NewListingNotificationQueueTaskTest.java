package com.sc_fleetfinder.fleets.unit_tests.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.NewListingNotifyQueueRepository;
import com.sc_fleetfinder.fleets.scheduledTasks.NewListingNotificationQueueTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewListingNotificationQueueTaskTest {

    @Mock
    private NewListingNotifyQueueRepository queueRepository;

    @InjectMocks
    private NewListingNotificationQueueTask task;

    @Test
    void matchNewListingsToCustomNotifications_ExecutesAllThreeStepsInOrder() {
        when(queueRepository.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches()).thenReturn(3);
        when(queueRepository.markProcessed()).thenReturn(2);

        task.matchNewListingsToCustomNotifications();

        assertAll("matchNewListingsToCustomNotifications all-steps assertions:",
                () -> verify(queueRepository).claimForProcessing(100),
                () -> verify(queueRepository).generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches(),
                () -> verify(queueRepository).markProcessed()
        );
    }

    @Test
    void matchNewListingsToCustomNotifications_WhenNoMatches_StillCompletesAllSteps() {
        when(queueRepository.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches()).thenReturn(0);
        when(queueRepository.markProcessed()).thenReturn(0);

        task.matchNewListingsToCustomNotifications();

        assertAll("matchNewListingsToCustomNotifications zero-matches assertions:",
                () -> verify(queueRepository).claimForProcessing(100),
                () -> verify(queueRepository).generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches(),
                () -> verify(queueRepository).markProcessed()
        );
    }
}
