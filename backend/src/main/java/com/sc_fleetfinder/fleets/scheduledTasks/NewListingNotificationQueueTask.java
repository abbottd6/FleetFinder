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
        queueRepository.claimForProcessing(batchSize);
        log.info("Claim duration: {} ms", System.currentTimeMillis() - claimStartTime);

        long generationStartTime = System.currentTimeMillis();
        int newOutboxEntries = queueRepository.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();
        log.info("Matching and outbox notification generation duration: {} ms",
                System.currentTimeMillis() - generationStartTime);

        log.info("The NewListingQueue created {} outbox entries for custom " +
                "notifications on all channels.", newOutboxEntries);


        int markedAs = this.queueRepository.markProcessed();
        log.info("Custom note matching task finished by marking {} new listings as " +
                "processed.", markedAs);
    }
}
