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
public class CleanupNewListingQueueTask {

    private final NewListingNotifyQueueRepository queueRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeProcessedNewListingQueueEntries() {
        int deleted = this.queueRepository.deleteOldProcessedEntries();

        log.info("Scheduled cleanup deleted {} processed 'new listing queue' " +
                "entries.", deleted);
    }

}
