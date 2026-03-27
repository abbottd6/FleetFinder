package com.sc_fleetfinder.fleets.scheduledTasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxCleanupTask {

    private final OutboxCleanupService outboxCleanupService;

    @Scheduled(cron = "0 30 3 * * *")
    public void cleanNotificationOutbox() {
        int archivesRemoved = 0;
        int outboxNotesArchived = 0;
        int outboxNotesDeleted = 0;

        //remove archived outbox entries that are older than 3 months
        try {
            archivesRemoved = this.outboxCleanupService.deleteExpiredArchives();
        } catch (Exception e) {
            logErrorAndStage("DELETE EXPIRED ARCHIVES",e.getMessage());
        }

        //archive notification outbox entries to be deleted (older than 1 day)
        try {
            outboxNotesArchived = this.outboxCleanupService.archiveOldNotificationOutboxEntries();
        } catch (Exception e) {
            logErrorAndStage("ARCHIVE OLD NOTIFICATION OUTBOX ENTRIES", e.getMessage());
        }

        //Delete recently archived notification outbox entries
        try {
            outboxNotesDeleted = this.outboxCleanupService.deleteRecentlyArchivedNotificationOutboxEntries();
        } catch (Exception e) {
            logErrorAndStage("DELETE RECENTLY ARCHIVED NOTIFICATION OUTBOX ENTRIES", e.getMessage());
        }

        //log info/counts
        if(archivesRemoved > 0) {
            log.info("cleanNotificationOutbox task deleted {} archives that were older than 3 months.", archivesRemoved);
        }

        if(outboxNotesArchived > 0 || outboxNotesDeleted > 0) {
            log.info("cleanNotificationOutbox task archived {} outbox notifications that were older than 3 days, " +
                    "and deleted {} from the notification_outbox table that were older than 3 days.",
                    outboxNotesArchived, outboxNotesDeleted);
        }

    }

    private void logErrorAndStage(String stage, String error) {
        log.error("The cleanNotificationOutbox task failed the {} stage with an exception: {}",stage,error);
    }
}
