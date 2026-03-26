package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OutboxTaskServiceTest {

    @Mock
    private NotificationOutboxRepository outboxRepo;

    @InjectMocks
    private OutboxTaskService outboxTaskService;

    @Test
    void claimPendingBatch_DelegatesToRepo() {
        // given
        when(outboxRepo.claimStatus_Pending(50)).thenReturn(7);

        // when
        int result = outboxTaskService.claimPendingBatch(50);

        // then
        assertAll("claimPendingBatch assertions:",
                () -> assertEquals(7, result),
                () -> verify(outboxRepo, times(1)).claimStatus_Pending(50)
        );
    }

    @Test
    void findStatus_Claimed_DelegatesToRepo() {
        // given
        NotificationOutbox mockOutbox = mock(NotificationOutbox.class);
        when(outboxRepo.findStatus_Claimed(50)).thenReturn(List.of(mockOutbox));

        // when
        List<NotificationOutbox> result = outboxTaskService.findStatus_Claimed(50);

        // then
        assertAll("findStatus_Claimed assertions:",
                () -> assertEquals(1, result.size()),
                () -> verify(outboxRepo, times(1)).findStatus_Claimed(50)
        );
    }

    @Test
    void markSent_DelegatesToRepo() {
        // given
        when(outboxRepo.markSent(5L)).thenReturn(1);

        // when
        int result = outboxTaskService.markSent(5L);

        // then
        assertAll("markSent assertions:",
                () -> assertEquals(1, result),
                () -> verify(outboxRepo, times(1)).markSent(5L)
        );
    }

    @Test
    void markForUserActive_Delayed_DelegatesToRepo() {
        // given
        when(outboxRepo.markForUserActive_Delayed(5L)).thenReturn(1);

        // when
        int result = outboxTaskService.markForUserActive_Delayed(5L);

        // then
        assertAll("markForUserActive_Delayed assertions:",
                () -> assertEquals(1, result),
                () -> verify(outboxRepo, times(1)).markForUserActive_Delayed(5L)
        );
    }

    @Test
    void markSkipped_DelegatesToRepo() {
        // given
        when(outboxRepo.markSkipped(5L, "skip reason")).thenReturn(1);

        // when
        int result = outboxTaskService.markSkipped(5L, "skip reason");

        // then
        assertAll("markSkipped assertions:",
                () -> assertEquals(1, result),
                () -> verify(outboxRepo, times(1)).markSkipped(5L, "skip reason")
        );
    }

    @Test
    void incrementAndCheckFailureCounter_ErrorCountBelow3_CallsStageFailureForRetry() {
        // given — errorCount is 1, after increment becomes 2 (< 3), so stageFailureForRetry is called
        // Use a real entity so setErrorCount() actually updates the value read by getErrorCount()
        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setErrorCount(1);
        outbox.setOutboxId(5L);
        when(outboxRepo.stageFailureForRetry(5L, "error")).thenReturn(1);

        // when
        int result = outboxTaskService.incrementAndCheckFailureCounter(outbox, "error");

        // then
        assertAll("below threshold → stageFailureForRetry:",
                () -> assertEquals(2, outbox.getErrorCount(), "errorCount should be incremented to 2"),
                () -> verify(outboxRepo, times(1)).stageFailureForRetry(5L, "error"),
                () -> verify(outboxRepo, never()).markFailed(anyLong(), any()),
                () -> assertEquals(1, result)
        );
    }

    @Test
    void incrementAndCheckFailureCounter_ErrorCountAt3_CallsMarkFailed() {
        // given — errorCount is 2, after increment becomes 3 (>= 3), so markFailed is called
        // Use a real entity so setErrorCount() actually updates the value read by getErrorCount()
        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setErrorCount(2);
        outbox.setOutboxId(5L);
        when(outboxRepo.markFailed(5L, "error")).thenReturn(1);

        // when
        int result = outboxTaskService.incrementAndCheckFailureCounter(outbox, "error");

        // then
        assertAll("at threshold → markFailed:",
                () -> assertEquals(3, outbox.getErrorCount(), "errorCount should be incremented to 3"),
                () -> verify(outboxRepo, times(1)).markFailed(5L, "error"),
                () -> verify(outboxRepo, never()).stageFailureForRetry(anyLong(), any()),
                () -> assertEquals(1, result)
        );
    }

    @Test
    void incrementAndCheckFailureCounter_ErrorCount0_StagesFailureForRetry() {
        // given — first ever failure: errorCount=0, after increment becomes 1 (< 3) → stageFailureForRetry
        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setErrorCount(0);
        outbox.setOutboxId(5L);
        when(outboxRepo.save(any())).thenReturn(outbox);
        when(outboxRepo.stageFailureForRetry(5L, "first error")).thenReturn(1);

        // when
        int result = outboxTaskService.incrementAndCheckFailureCounter(outbox, "first error");

        // then
        assertAll("first failure (count 0→1) → stageFailureForRetry:",
                () -> assertEquals(1, outbox.getErrorCount(), "errorCount should be incremented to 1"),
                () -> verify(outboxRepo, times(1)).stageFailureForRetry(5L, "first error"),
                () -> verify(outboxRepo, never()).markFailed(anyLong(), any()),
                () -> assertEquals(1, result)
        );
    }

    @Test
    void incrementAndCheckFailureCounter_ErrorCountAbove3_CallsMarkFailed() {
        // given — errorCount already at 3 (somehow repeated), increment → 4 (>= 3) → markFailed
        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setErrorCount(3);
        outbox.setOutboxId(5L);
        when(outboxRepo.markFailed(5L, "error")).thenReturn(1);

        // when
        int result = outboxTaskService.incrementAndCheckFailureCounter(outbox, "error");

        // then
        assertAll("count already above threshold (3→4) → markFailed:",
                () -> assertEquals(4, outbox.getErrorCount()),
                () -> verify(outboxRepo, times(1)).markFailed(5L, "error"),
                () -> verify(outboxRepo, never()).stageFailureForRetry(anyLong(), any()),
                () -> assertEquals(1, result)
        );
    }

    @Test
    void incrementAndCheckFailureCounter_ErrorCountBelow3_SavesEntityBeforeStaging() {
        // given — verifies outboxRepo.save(outbox) is called BEFORE stageFailureForRetry
        NotificationOutbox outbox = new NotificationOutbox();
        outbox.setErrorCount(1);
        outbox.setOutboxId(5L);
        when(outboxRepo.save(any())).thenReturn(outbox);
        when(outboxRepo.stageFailureForRetry(5L, "error")).thenReturn(1);

        // when
        outboxTaskService.incrementAndCheckFailureCounter(outbox, "error");

        // then — verify call order
        InOrder inOrder = inOrder(outboxRepo);
        inOrder.verify(outboxRepo).save(outbox);
        inOrder.verify(outboxRepo).stageFailureForRetry(5L, "error");
    }

    @Test
    void markFailed_DelegatesToRepo() {
        // OutboxTaskService.markFailed(Long, String) currently has zero test coverage
        outboxTaskService.markFailed(5L, "terminal error");
        verify(outboxRepo, times(1)).markFailed(5L, "terminal error");
    }
}
