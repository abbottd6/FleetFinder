package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledArchiveService {

    private final GroupListingRepository listingRepo;
    private final ListingArchiveRepository archiveRepo;

    @Transactional
    public int createOutboxEntriesForArchiveNotifications() {
        return listingRepo.createOutboxEntriesForArchiveNotifications();
    }

    @Transactional
    public int setArchivedStatus() {
        return listingRepo.setArchivedStatus();
    }

    @Transactional
    public int generateArchivesForExpired() {
        return archiveRepo.generateArchivesForScheduledRemoval();
    }

    @Transactional
    public int deleteArchivedListings() {
        return listingRepo.scheduledDeleteArchivedListings();
    }

}
