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
    protected void markSent(long outboxId) {
        outboxRepo.markSent(outboxId);
    }

    @Transactional
    protected void markFailed(long outboxId, String msg) {
        outboxRepo.markFailed(outboxId, msg);
    }
}
