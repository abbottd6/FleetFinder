package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxTaskService {

    private final NotificationOutboxRepository outboxRepo;

    @Transactional
    protected int claimPendingBatch(int batchSize) {
        return outboxRepo.claimStatus_Pending(batchSize);
    }

    @Transactional(readOnly = true)
    protected List<NotificationOutbox> findStatus_Claimed(int batchSize) {
        return outboxRepo.findStatus_Claimed(batchSize);
    }

    @Transactional
    protected int markForUserActive_Delayed(Long outboxId) {
        return outboxRepo.markForUserActive_Delayed(outboxId);
    }

    @Transactional
    protected int markSent(long outboxId) {
        return outboxRepo.markSent(outboxId);
    }

    @Transactional
    protected int incrementAndCheckFailureCounter(NotificationOutbox outbox, String errorMessage) {
        outbox.setErrorCount(outbox.getErrorCount() + 1);

        if(outbox.getErrorCount() >= 3) {
            return outboxRepo.markFailed(outbox.getOutboxId(), errorMessage);
        }

        outboxRepo.save(outbox);

        return outboxRepo.stageFailureForRetry(outbox.getOutboxId(), errorMessage);
    }

    @Transactional
    protected int markSkipped(Long outboxId, String msg) {
        return outboxRepo.markSkipped(outboxId, msg);
    }

    @Transactional
    protected void markFailed(Long outboxId, String errorMsg) {
        outboxRepo.markFailed(outboxId, errorMsg);
    }
}
