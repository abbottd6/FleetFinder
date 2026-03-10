package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void markFailed_DelegatesToRepo() {
        // given
        when(outboxRepo.markFailed(5L, "error")).thenReturn(1);

        // when
        int result = outboxTaskService.markFailed(5L, "error");

        // then
        assertAll("markFailed assertions:",
                () -> assertEquals(1, result),
                () -> verify(outboxRepo, times(1)).markFailed(5L, "error")
        );
    }
}
