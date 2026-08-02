package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.NewListingNotifyQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewListingNotificationQueueTask {

    private final NewListingNotifyQueueRepository queueRepository;

    @Scheduled(fixedDelayString = "PT2M")
    @Transactional
    public void matchNewListingsToCustomNotifications() {
        int batchSize = 100;

        long claimStartTime = System.currentTimeMillis();
        int claimed = queueRepository.claimForProcessing(batchSize);
        if(claimed > 0) {
            log.info("Claim duration: {} ms", System.currentTimeMillis() - claimStartTime);
            log.info("New listings claimed for Custom Notification matching: {}", claimed);
        }

        long generationStartTime = System.currentTimeMillis();
        int newOutboxEntries = queueRepository.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        if(newOutboxEntries > 0) {
            log.info("Matching and outbox notification generation duration: {} ms",
                    System.currentTimeMillis() - generationStartTime);

            log.info("The NewListingQueue created {} outbox entries for custom " +
                    "notifications on all channels.", newOutboxEntries);
        }

        int markedAs = this.queueRepository.markProcessed();
        if(markedAs > 0) {
            log.info("Custom note matching task finished by marking {} new listings as " +
                    "processed.", markedAs);
        }
    }
}
