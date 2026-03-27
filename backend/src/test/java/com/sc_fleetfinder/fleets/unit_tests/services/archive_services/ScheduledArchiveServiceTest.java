package com.sc_fleetfinder.fleets.unit_tests.services.archive_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.scheduledTasks.ScheduledArchiveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledArchiveServiceTest {

    @Mock
    private GroupListingRepository listingRepo;

    @Mock
    private ListingArchiveRepository archiveRepo;

    @InjectMocks
    private ScheduledArchiveService scheduledArchiveService;

    @Test
    void createOutboxEntriesForArchiveNotifications_DelegatesToListingRepo() {
        // given
        when(listingRepo.createOutboxEntriesForArchiveNotifications()).thenReturn(3);

        // when
        int result = scheduledArchiveService.createOutboxEntriesForArchiveNotifications();

        // then
        assertAll("createOutboxEntriesForArchiveNotifications assertions:",
                () -> assertEquals(3, result),
                () -> verify(listingRepo, times(1)).createOutboxEntriesForArchiveNotifications(),
                () -> verifyNoMoreInteractions(archiveRepo)
        );
    }

    @Test
    void setArchivedStatus_DelegatesToListingRepo() {
        // given
        when(listingRepo.setArchivedStatus()).thenReturn(2);

        // when
        int result = scheduledArchiveService.setArchivedStatus();

        // then
        assertAll("setArchivedStatus assertions:",
                () -> assertEquals(2, result),
                () -> verify(listingRepo, times(1)).setArchivedStatus()
        );
    }

    @Test
    void generateArchivesForExpired_DelegatesToArchiveRepo() {
        // given
        when(archiveRepo.generateArchivesForScheduledRemoval()).thenReturn(5);

        // when
        int result = scheduledArchiveService.generateArchivesForExpired();

        // then
        assertAll("generateArchivesForExpired assertions:",
                () -> assertEquals(5, result),
                () -> verify(archiveRepo, times(1)).generateArchivesForScheduledRemoval(),
                () -> verifyNoMoreInteractions(listingRepo)
        );
    }

    @Test
    void deleteArchivedListings_DelegatesToListingRepo() {
        // given
        when(listingRepo.scheduledDeleteArchivedListings()).thenReturn(5);

        // when
        int result = scheduledArchiveService.deleteArchivedListings();

        // then
        assertAll("deleteArchivedListings assertions:",
                () -> assertEquals(5, result),
                () -> verify(listingRepo, times(1)).scheduledDeleteArchivedListings()
        );
    }
}
