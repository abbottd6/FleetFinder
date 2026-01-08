package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredListingDeleteTask {

    private final GroupListingRepository listingRepo;

    //TODO ADJUST THIS RATE
    @Scheduled(fixedDelayString= "PT2M")
    @Transactional
    public void deleteExpiredListing() {
        int newOutboxEntities = listingRepo.createOutboxEntriesForArchiveNotifications();

        log.info("Added {} outbox entries for expired listings " +
                 "queued for deletion.", newOutboxEntities);

        int statusArchivedCount = listingRepo.setArchivedStatus();


    }
}
