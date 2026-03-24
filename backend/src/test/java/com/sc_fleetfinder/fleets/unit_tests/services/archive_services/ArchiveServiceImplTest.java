package com.sc_fleetfinder.fleets.unit_tests.services.archive_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveServiceImpl;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveServiceImplTest {

    @Mock
    private ListingArchiveRepository lar;

    @Mock
    private ModerationIssueRepository mir;

    @Mock
    private GroupListingRepository groupListingRepository;

    @InjectMocks
    private ArchiveServiceImpl archiveService;

    // Builds a fully-stubbed GroupListing mock so the ListingArchive constructor does not NPE
    private GroupListing buildMockListing() {
        GroupListing listing = mock(GroupListing.class);
        Users user = mock(Users.class);
        when(user.getUserId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("TestUser");
        when(listing.getGroupId()).thenReturn(1L);
        when(listing.getUsers()).thenReturn(user);
        when(listing.getListingTitle()).thenReturn("Test Listing Title");
        when(listing.getListingDescription()).thenReturn("Test listing description");
        when(listing.getAvailableRoles()).thenReturn("Any");
        when(listing.getCommsService()).thenReturn("Discord");
        when(listing.getServer()).thenReturn(mock(ServerRegion.class));
        when(listing.getEnvironment()).thenReturn(mock(GameEnvironment.class));
        when(listing.getExperience()).thenReturn(mock(GameExperience.class));
        when(listing.getPlayStyle()).thenReturn(null);
        when(listing.getLegality()).thenReturn(mock(Legality.class));
        when(listing.getGroupStatus()).thenReturn(mock(GroupStatus.class));
        when(listing.getEventSchedule()).thenReturn(null);
        when(listing.getCategory()).thenReturn(mock(GameplayCategory.class));
        when(listing.getSubcategory()).thenReturn(null);
        when(listing.getPvpStatus()).thenReturn(mock(PvpStatus.class));
        when(listing.getSystem()).thenReturn(mock(PlanetarySystem.class));
        when(listing.getPlanetMoonSystem()).thenReturn(null);
        when(listing.getCurrentPartySize()).thenReturn(1);
        when(listing.getDesiredPartySize()).thenReturn(3);
        when(listing.getCommsOption()).thenReturn("Optional");
        when(listing.getCreationTimestamp()).thenReturn(Instant.now());
        when(listing.getLastUpdated()).thenReturn(Instant.now());
        return listing;
    }

    // Builds a ModerationIssue mock with all count stubs returning 0
    private ModerationIssue buildMockIssue() {
        ModerationIssue issue = mock(ModerationIssue.class);
        when(issue.getReportTotalCount()).thenReturn(0);
        when(issue.getSpamCount()).thenReturn(0);
        when(issue.getHateSpeechCount()).thenReturn(0);
        when(issue.getNsfwCount()).thenReturn(0);
        when(issue.getScamCount()).thenReturn(0);
        when(issue.getOffTopicCount()).thenReturn(0);
        when(issue.getTrollCount()).thenReturn(0);
        when(issue.getDoxxCount()).thenReturn(0);
        when(issue.getCheatCount()).thenReturn(0);
        when(issue.getOtherCount()).thenReturn(0);
        when(issue.getStatus()).thenReturn("Pending");
        return issue;
    }

    @Test
    void prepareUserDeleteRecords_WhenModerationIssueExists_ArchivesWithPendingNote() {
        LogCaptor logCaptor = LogCaptor.forClass(ArchiveServiceImpl.class);

        // given
        GroupListing listing = buildMockListing();
        Users user = listing.getUsers();
        ModerationIssue existingIssue = buildMockIssue();

        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(existingIssue));
        when(lar.save(any(ListingArchive.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        archiveService.prepareUserDeleteRecords(listing, user);

        // then - capture the saved archive for field inspection
        ArgumentCaptor<ListingArchive> archiveCaptor = ArgumentCaptor.forClass(ListingArchive.class);
        verify(lar, atLeast(1)).save(archiveCaptor.capture());
        ListingArchive capturedArchive = archiveCaptor.getValue();

        assertAll("prepareUserDeleteRecords with existing moderation issue assertions:",
                () -> verify(mir, times(1)).findByGroupRef(listing),
                () -> verify(mir, never()).save(any(ModerationIssue.class)),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Archive records generated for listing:")),
                        "Expected info log for archive creation"),
                () -> assertEquals("None", capturedArchive.getActionType(),
                        "Archive actionType should be 'None' for user-initiated delete"),
                () -> assertNull(capturedArchive.getModId(),
                        "modId should be null for non-manual archive")
        );
    }

    @Test
    void prepareUserDeleteRecords_WhenNoModerationIssueExists_CreatesIssueAndArchives() {
        LogCaptor logCaptor = LogCaptor.forClass(ArchiveServiceImpl.class);

        // given
        GroupListing listing = buildMockListing();
        Users user = listing.getUsers();

        when(mir.findByGroupRef(listing)).thenReturn(Optional.empty());
        when(mir.save(any(ModerationIssue.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lar.save(any(ListingArchive.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        archiveService.prepareUserDeleteRecords(listing, user);

        // then - capture saved entities for inspection
        ArgumentCaptor<ModerationIssue> issueCaptor = ArgumentCaptor.forClass(ModerationIssue.class);
        ArgumentCaptor<ListingArchive> archiveCaptor = ArgumentCaptor.forClass(ListingArchive.class);

        verify(mir, times(1)).save(issueCaptor.capture());
        verify(lar, atLeast(1)).save(archiveCaptor.capture());

        ModerationIssue capturedIssue = issueCaptor.getValue();
        ListingArchive capturedArchive = archiveCaptor.getValue();

        assertAll("prepareUserDeleteRecords with no existing moderation issue assertions:",
                () -> assertEquals("No Reports", capturedIssue.getStatus(),
                        "Newly created ModerationIssue should have status 'No Reports'"),
                () -> assertTrue(logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("Archive records generated for listing:")),
                        "Expected info log for archive creation"),
                () -> assertEquals("None", capturedArchive.getActionType(),
                        "Archive actionType should be 'None' for user-initiated delete")
        );
    }

    @Test
    void archiveListing_AutoVariant_PersistsWithCorrectActionType() {
        // given
        GroupListing listing = buildMockListing();
        ModerationIssue issue = buildMockIssue();
        String note = "User deleted listing with no recorded moderation issue.";

        when(lar.save(any(ListingArchive.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ListingArchive result = archiveService.archiveListing(listing, issue, "AutoMod", note);

        // then
        ArgumentCaptor<ListingArchive> archiveCaptor = ArgumentCaptor.forClass(ListingArchive.class);
        verify(lar, times(1)).save(archiveCaptor.capture());
        ListingArchive capturedArchive = archiveCaptor.getValue();

        assertAll("archiveListing auto variant assertions:",
                () -> assertNotNull(result, "archiveListing should return a non-null archive"),
                () -> assertEquals("AutoMod", capturedArchive.getActionType(),
                        "Auto variant should set actionType to 'AutoMod'"),
                () -> assertNull(capturedArchive.getModId(),
                        "Auto variant should have null modId")
        );
    }

    @Test
    void archiveListing_ManualVariant_PersistsWithModInfo() {
        // given
        GroupListing listing = buildMockListing();
        ModerationIssue issue = buildMockIssue();
        String modNote = "Listing removed by moderator for policy violation.";

        Users mod = new Users();
        mod.setUserId(99L);
        mod.setUsername("modUser");

        when(lar.save(any(ListingArchive.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ListingArchive result = archiveService.archiveListing(listing, issue, modNote, mod);

        // then
        ArgumentCaptor<ListingArchive> archiveCaptor = ArgumentCaptor.forClass(ListingArchive.class);
        verify(lar, times(1)).save(archiveCaptor.capture());
        ListingArchive capturedArchive = archiveCaptor.getValue();

        assertAll("archiveListing manual variant assertions:",
                () -> assertNotNull(result, "archiveListing should return a non-null archive"),
                () -> assertEquals("Manual", capturedArchive.getActionType(),
                        "Manual variant should set actionType to 'Manual'"),
                () -> assertEquals(99L, capturedArchive.getModId(),
                        "Moderator userId should be captured in archive"),
                () -> assertEquals("modUser", capturedArchive.getModname(),
                        "Moderator username should be captured in archive")
        );
    }
}
