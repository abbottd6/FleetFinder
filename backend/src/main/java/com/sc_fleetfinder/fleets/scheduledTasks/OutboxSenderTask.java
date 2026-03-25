package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.exceptions.SkipExternalNotificationProcessingException;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.utils.DeliveryChannel;
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
    private final UserActivityCache activityCache;

    private final int RECENT_THRESHOLD = 300;

    @Scheduled(fixedDelayString = "PT30S")
    public void sendOutboxNotifications() {
        int sentCount = 0;
        int skippedCount = 0;
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
                if(!outbox.getDeliveryChannel().equals(DeliveryChannel.IN_APP)) {
                    String kcId = outbox.getEntityOwner().getKeycloakId();
                    if (activityCache.hasRecentAccess(kcId, RECENT_THRESHOLD) && (outbox.getAttemptCount() < 10)) {
                        outbox.setStatus("PENDING");
                        outbox.setLastError("User recently active.");
                        outbox.setAttemptCount(outbox.getAttemptCount() + 1);
                        log.warn("INCREMENTED");
                        continue;
                    }
                }

                notificationService.prepareAndSendOutboxNotification(outbox);
                outboxService.markSent(outbox.getOutboxId());
                ++sentCount;

            } catch (SkipExternalNotificationProcessingException e) {
                String msg = e.getMessage();
                outboxService.markSkipped(outbox.getOutboxId(), msg);
                ++skippedCount;
            } catch (Exception e) {
                String msg = e.getMessage();
                if(msg != null && msg.length() > 900) msg = msg.substring(0,900);
                outboxService.markFailed(outbox.getOutboxId(), msg);
                ++failedCount;
            }
        }

        log.debug("Sent: {} outbox notifications were sent.", sentCount);
        log.debug("Skipped: {} outbox notifications were skipped.", skippedCount);
        log.debug("Failed: {} outbox notifications failed to send.", failedCount);
    }
}
