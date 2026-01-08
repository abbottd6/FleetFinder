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
        final int batchSize = 100;

        int claimed = outboxService.claimPendingBatch(batchSize);

        if (claimed == 0) {
            return;
        }

        List<NotificationOutbox> batch = outboxService.findStatus_Claimed(batchSize);
        for(NotificationOutbox outbox : batch) {
            try {
                notificationService.sendOutboxNotification(outbox);
                outboxService.markSent(outbox.getOutboxId());
            } catch (Exception e) {
                String msg = e.getMessage();
                if(msg != null && msg.length() > 900) msg = msg.substring(0,900);
                outboxService.markFailed(outbox.getOutboxId(), msg);
            }
        }
    }
}
