package com.sc_fleetfinder.fleets.scheduledTasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredListingDeleteTask {

    private final ScheduledArchiveService scheduledArchiveService;

    //TODO ADJUST THIS RATE
    @Scheduled(fixedDelayString= "PT10M")
    @Transactional
    public void archiveExpiredListing() {
        int statusChangedCount = scheduledArchiveService.setArchivedStatus();

        log.info("Changed the status of {} listings in preparation for archiving.", statusChangedCount);

        int archivedCount = scheduledArchiveService.generateArchivesForExpired();

        log.info("Archived {} listings for scheduled deletion.", archivedCount);

        int newOutboxEntities = scheduledArchiveService.createOutboxEntriesForArchiveNotifications();

        log.info("Added {} outbox entries for expired listings " +
                 "queued for deletion.", newOutboxEntities);

        int deletedCount = scheduledArchiveService.deleteArchivedListings();

        log.info("Deleted {} listings after archival.", deletedCount);
    }
}
