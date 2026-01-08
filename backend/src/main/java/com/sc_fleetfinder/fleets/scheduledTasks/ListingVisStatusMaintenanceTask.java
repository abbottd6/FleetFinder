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
public class ListingVisStatusMaintenanceTask {

    private final GroupListingRepository listingRepo;

    @Scheduled(fixedDelayString= "PT5M")
    @Transactional
    public void updateListingVisStatuses() {
        int newOutboxEntities = listingRepo.createNotificationOutboxEntriesForStatusUpdates();

        log.info("Update listing vis_status generated {} new outbox entries.", newOutboxEntities);

        int statusesUpdated = listingRepo.updateListingVisStatuses();

        log.info("Updated the vis_status of {} listings.", statusesUpdated);
    }
}
