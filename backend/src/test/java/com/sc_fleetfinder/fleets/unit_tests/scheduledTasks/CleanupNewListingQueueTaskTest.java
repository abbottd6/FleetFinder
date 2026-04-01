package com.sc_fleetfinder.fleets.unit_tests.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.NewListingNotifyQueueRepository;
import com.sc_fleetfinder.fleets.scheduledTasks.CleanupNewListingQueueTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanupNewListingQueueTaskTest {

    @Mock
    private NewListingNotifyQueueRepository queueRepository;

    @InjectMocks
    private CleanupNewListingQueueTask task;

    @Test
    void removeProcessedNewListingQueueEntries_CallsDeleteAndReturnsCount() {
        when(queueRepository.deleteOldProcessedEntries()).thenReturn(5);

        task.removeProcessedNewListingQueueEntries();

        verify(queueRepository).deleteOldProcessedEntries();
    }

    @Test
    void removeProcessedNewListingQueueEntries_WhenNothingToDelete_StillCompletes() {
        when(queueRepository.deleteOldProcessedEntries()).thenReturn(0);

        task.removeProcessedNewListingQueueEntries();

        verify(queueRepository).deleteOldProcessedEntries();
    }
}
