package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxSenderTask {

    private final NotificationService notificationService;
    private final OutboxTaskService outboxService;

    @Scheduled(fixedDelayString = "PT30S")
    public void sendOutboxNotifications() {
        int sentCount = 0;
        int failedCount = 0;

        final int BATCH_SIZE = 100;

        int claimed = outboxService.claimPendingBatch(BATCH_SIZE);

        log.info("claimed {} outbox notifications for processing.", claimed);

        List<NotificationOutbox> batch = outboxService.findStatus_Claimed(BATCH_SIZE);

        if(batch.isEmpty()) {
            return;
        }
        for(NotificationOutbox outbox : batch) {
            try {
                notificationService.sendOutboxNotification(outbox);
                outboxService.markSent(outbox.getOutboxId());
                ++sentCount;
            } catch (Exception e) {
                String msg = e.getMessage();
                if(msg != null && msg.length() > 900) msg = msg.substring(0,900);
                outboxService.markFailed(outbox.getOutboxId(), msg);
                ++failedCount;
            }
        }

        log.info("Sent: {} outbox notifications were sent.", sentCount);
        log.info("Failed: {} outbox notifications failed to send.", failedCount);
    }
}
