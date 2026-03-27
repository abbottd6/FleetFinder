package com.sc_fleetfinder.fleets.unit_tests.services.mod_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportBasisRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModListingActionRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.UserModerationRecordRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ManualModDeleteDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ModClearIssueDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.UserModerationRecord;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.ListingModDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions.ModListingActionConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions.ModerationIssueConversionService;
import com.sc_fleetfinder.fleets.services.mod_services.ModerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationConstants.AUTO_MOD_REPORT_THRESHOLD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModerationServiceImplTest {

    @Mock
    private ArchiveService archiveService;
    @Mock
    private GroupListingRepository glr;
    @Mock
    private GroupListingConversionService glcs;
    @Mock
    private UserRepository userRepo;
    @Mock
    private UserModerationRecordRepository umrr;
    @Mock
    private ListingReportRepository lrr;
    @Mock
    private ModListingActionRepository mlar;
    @Mock
    private ModerationIssueRepository mir;
    @Mock
    private ModerationIssueConversionService mics;
    @Mock
    private ModListingActionConversionService mlacs;
    @Mock
    private ListingReportBasisRepository lrbr;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private NotificationService noteService;

    @InjectMocks
    private ModerationServiceImpl moderationService;

    @BeforeEach
    void injectArchiveService() {
        // ModerationServiceImpl uses constructor injection for most deps, but archiveService
        // is @Autowired (field-only). Mockito's constructor injection skips field injection,
        // so we must inject it manually after @InjectMocks creates the instance.
        ReflectionTestUtils.setField(moderationService, "archiveService", archiveService);
    }

    // Builds a fully-stubbed GroupListing mock so entity constructors do not NPE
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

    // ─── Read / Pageable ────────────────────────────────────────────────────────

    @Test
    void modGetAllGroupListings_ReturnsPagedDtos() {
        // given
        GroupListing listing = buildMockListing();
        GroupListingResponseDto dto = new GroupListingResponseDto();
        dto.setGroupId(1L);
        Page<GroupListing> page = new PageImpl<>(List.of(listing));

        when(glr.findAll(any(Pageable.class))).thenReturn(page);
        when(glcs.convertListingToResponseDto(listing)).thenReturn(dto);

        // when
        Page<GroupListingResponseDto> result = moderationService.modGetAllGroupListings(Pageable.unpaged());

        // then
        assertAll("modGetAllGroupListings paged result:",
                () -> assertFalse(result.isEmpty(), "Result page should not be empty"),
                () -> assertEquals(1L, result.getContent().getFirst().getGroupId(),
                        "Returned DTO groupId should match mocked value")
        );
    }

    @Test
    void modGetAllIssues_ReturnsPagedIssueDtos() {
        // given
        ModerationIssue issue = buildMockIssue();
        ModerationIssueResponseDto dto = new ModerationIssueResponseDto();
        dto.setIssueId(1L);
        Page<ModerationIssue> page = new PageImpl<>(List.of(issue));

        when(mir.findAll(any(Pageable.class))).thenReturn(page);
        when(mics.convertToResponseDto(issue)).thenReturn(dto);

        // when
        Page<ModerationIssueResponseDto> result = moderationService.modGetAllIssues(Pageable.unpaged());

        // then
        assertAll("modGetAllIssues paged result:",
                () -> assertFalse(result.isEmpty(), "Result page should not be empty"),
                () -> assertEquals(1L, result.getContent().getFirst().getIssueId(),
                        "Returned DTO issueId should match mocked value")
        );
    }

    @Test
    void getThisWeeksModListingActions_DelegatesToRepo_WithSevenDayCutoff() {
        // given
        ModListingAction action = mock(ModListingAction.class);
        ModListingActionDto dto = new ModListingActionDto();
        dto.setActionId(1L);
        Page<ModListingAction> page = new PageImpl<>(List.of(action));

        when(mlar.findWeeksActions(any(Instant.class), any(Pageable.class))).thenReturn(page);
        when(mlacs.convertToDto(action)).thenReturn(dto);

        // when
        Page<ModListingActionDto> result = moderationService.getThisWeeksModListingActions(Pageable.unpaged());

        // then
        assertAll("getThisWeeksModListingActions result:",
                () -> assertFalse(result.isEmpty(), "Result page should not be empty"),
                () -> verify(mlar, times(1)).findWeeksActions(any(Instant.class), any(Pageable.class))
        );
    }

    // ─── modDeleteListing ────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void modDeleteListing_ListingExists_ReturnsOkWithTitle() {
        // given
        GroupListing listing = buildMockListing();

        Users mod = new Users();
        mod.setUserId(99L);
        mod.setUsername("modUser");

        ManualModDeleteDto dto = new ManualModDeleteDto();
        dto.setGroupId(1L);
        dto.setReportBasis(1);
        dto.setNote("Policy violation");

        Users owner = new Users();
        owner.setUserId(1L);
        owner.setUsername("TestUser");

        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(basis.getBasisLabel()).thenReturn("Spam");

        ListingArchive archive = mock(ListingArchive.class);
        UserModerationRecord record = mock(UserModerationRecord.class);
        when(record.getModActionCount()).thenReturn(0);
        when(record.getLastModActionTs()).thenReturn(Instant.now());

        when(glr.findById(anyLong())).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(any())).thenReturn(Optional.empty());
        when(mir.save(any(ModerationIssue.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lrbr.findById(any())).thenReturn(Optional.of(basis));
        when(archiveService.archiveListing(any(), any(), any(), any(Users.class))).thenReturn(archive);
        when(mlar.save(any(ModListingAction.class))).thenReturn(mock(ModListingAction.class));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(owner));
        when(umrr.findByUser(any(Users.class))).thenReturn(Optional.of(record));

        // when
        ResponseEntity<?> response = moderationService.modDeleteListing(dto, mod);

        // then
        assertAll("modDeleteListing success assertions:",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(),
                        "Response should be 200 OK"),
                () -> {
                    Map<String, String> body = (Map<String, String>) response.getBody();
                    assertNotNull(body, "Response body should not be null");
                    assertTrue(body.containsKey("listingTitle"),
                            "Response body should contain 'listingTitle' key");
                }
        );
    }

    @Test
    void modDeleteListing_ListingNotFound_Returns500() {
        // given — listing not found → ResourceNotFoundException wrapped in try/catch → 500
        when(glr.findById(anyLong())).thenReturn(Optional.empty());

        ManualModDeleteDto dto = new ManualModDeleteDto();
        dto.setGroupId(999L);
        dto.setReportBasis(1);

        Users mod = new Users();
        mod.setUserId(99L);
        mod.setUsername("modUser");

        // when
        ResponseEntity<?> response = moderationService.modDeleteListing(dto, mod);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(),
                "ResourceNotFoundException inside try/catch should yield 500");
    }

    // ─── modClearIssue ───────────────────────────────────────────────────────────

    @Test
    void modClearIssue_IssueExists_ResetsAllCountersAndRecordsAction() {
        // given — real ModerationIssue so field values can be inspected after the call
        ModerationIssue issue = new ModerationIssue();
        GroupListing mockListing = mock(GroupListing.class);
        when(mockListing.getGroupId()).thenReturn(1L);
        Users mockUser = mock(Users.class);
        when(mockUser.getUserId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn("TestUser");

        issue.setGroupRef(mockListing);
        issue.setUserRef(mockUser);
        issue.setSpamCount(5);
        issue.setHateSpeechCount(3);
        issue.setNsfwCount(1);
        issue.setScamCount(0);
        issue.setOffTopicCount(0);
        issue.setTrollCount(0);
        issue.setDoxxCount(0);
        issue.setCheatCount(0);
        issue.setOtherCount(0);
        issue.setReportTotalCount(9);
        issue.setStatus("Pending");

        ModClearIssueDto dto = new ModClearIssueDto();
        dto.setIssueId(1L);
        dto.setNote("Cleared by moderator");

        Users mod = new Users();
        mod.setUserId(99L);
        mod.setUsername("modUser");

        when(mir.findById(dto.getIssueId())).thenReturn(Optional.of(issue));
        when(mlar.save(any(ModListingAction.class))).thenReturn(mock(ModListingAction.class));

        // when
        ResponseEntity<?> response = moderationService.modClearIssue(dto, mod);

        // then
        assertAll("modClearIssue success — all counters reset and status set:",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(0, issue.getReportTotalCount()),
                () -> assertEquals(0, issue.getSpamCount()),
                () -> assertEquals(0, issue.getHateSpeechCount()),
                () -> assertEquals(0, issue.getNsfwCount()),
                () -> assertEquals(0, issue.getScamCount()),
                () -> assertEquals(0, issue.getOffTopicCount()),
                () -> assertEquals(0, issue.getTrollCount()),
                () -> assertEquals(0, issue.getDoxxCount()),
                () -> assertEquals(0, issue.getCheatCount()),
                () -> assertEquals(0, issue.getOtherCount()),
                () -> assertEquals("Cleared", issue.getStatus()),
                () -> verify(mir, atLeast(1)).save(issue),
                () -> verify(mlar, times(1)).save(any(ModListingAction.class))
        );
    }

    @Test
    void modClearIssue_IssueNotFound_Returns500() {
        // given
        when(mir.findById(any())).thenReturn(Optional.empty());

        ModClearIssueDto dto = new ModClearIssueDto();
        dto.setIssueId(999L);

        // when
        ResponseEntity<?> response = moderationService.modClearIssue(dto, new Users());

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(),
                "ResourceNotFoundException inside try/catch should yield 500");
    }

    // ─── prepareAutoModRemovalRecords ────────────────────────────────────────────

    @Test
    void prepareAutoModRemovalRecords_MarkReportsActioned_ArchivesAndPublishesEvent() {
        // given
        ModerationIssue issue = buildMockIssue();
        Users mockUserRef = mock(Users.class);
        when(mockUserRef.getUserId()).thenReturn(1L);
        when(mockUserRef.getUsername()).thenReturn("TestUser");
        when(issue.getUserRef()).thenReturn(mockUserRef);
        when(issue.getIssueId()).thenReturn(1L);

        GroupListing condemned = buildMockListing();
        when(issue.getGroupRef()).thenReturn(condemned);

        Users owner = new Users();
        owner.setUserId(1L);
        owner.setUsername("TestUser");

        ListingReport report = mock(ListingReport.class);
        Set<ListingReport> reports = new HashSet<>(Set.of(report));

        ListingArchive archive = mock(ListingArchive.class);
        UserModerationRecord record = mock(UserModerationRecord.class);
        when(record.getModActionCount()).thenReturn(0);
        when(record.getLastModActionTs()).thenReturn(Instant.now());

        when(userRepo.findById(1L)).thenReturn(Optional.of(owner));
        when(glr.findById(1L)).thenReturn(Optional.of(condemned));
        when(lrr.findByModIssueRef(issue)).thenReturn(reports);
        when(lrr.saveAll(any())).thenReturn(List.of(report));
        when(archiveService.archiveListing(any(GroupListing.class), any(ModerationIssue.class),
                any(String.class),any(String.class))).thenReturn(archive);
        when(mir.findAutoModActionBasis_MostCommonReportBasis(anyLong()))
                .thenReturn(mock(ListingReportBasis.class));
        when(mlar.save(any(ModListingAction.class))).thenReturn(mock(ModListingAction.class));
        when(umrr.findByUser(any(Users.class))).thenReturn(Optional.of(record));

        // when
        moderationService.prepareAutoModRemovalRecords(issue);

        // then
        assertAll("prepareAutoModRemovalRecords side-effects:",
                () -> verify(lrr, times(1)).saveAll(any()),
                () -> verify(archiveService, times(1)).archiveListing(
                        any(GroupListing.class), any(ModerationIssue.class), any(String.class), any(String.class)),
                () -> verify(eventPublisher, times(1)).publishEvent(any(ListingModDeleteEvent.class))
        );
    }

    // ─── generateModerationIssue ─────────────────────────────────────────────────

    @Test
    void generateModerationIssue_WhenNoExistingIssue_CreatesAndSavesIssue() {
        // given
        GroupListing listing = buildMockListing();
        when(mir.findByGroupRef(listing)).thenReturn(Optional.empty());
        when(mir.save(any(ModerationIssue.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ModerationIssue result = moderationService.generateModerationIssue(listing, "Pending");

        // then
        assertAll("generateModerationIssue with no existing issue:",
                () -> assertNotNull(result, "Returned issue should not be null"),
                () -> assertEquals("Pending", result.getStatus(),
                        "Issue status should match the provided status string"),
                () -> verify(mir, times(1)).save(any(ModerationIssue.class))
        );
    }

    @Test
    void generateModerationIssue_WhenIssueAlreadyExists_ThrowsIllegalState() {
        // given
        GroupListing listing = buildMockListing();
        ModerationIssue existingIssue = buildMockIssue();
        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(existingIssue));

        // when / then
        assertThrows(IllegalStateException.class,
                () -> moderationService.generateModerationIssue(listing, "Pending"),
                "Should throw IllegalStateException when a ModerationIssue already exists for the listing");
    }

    // ─── updateOrCreateUserModerationRecord ──────────────────────────────────────

    @Test
    void updateOrCreateUserModerationRecord_ExistingRecord_IncrementsCount() {
        // given
        GroupListing listing = buildMockListing(); // getUsers().getUserId() = 1L

        Users user = new Users();
        user.setUserId(1L);

        UserModerationRecord record = mock(UserModerationRecord.class);
        when(record.getModActionCount()).thenReturn(2);
        when(record.getLastModActionTs()).thenReturn(Instant.now());

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(umrr.findByUser(user)).thenReturn(Optional.of(record));

        // when
        moderationService.updateOrCreateUserModerationRecord(listing);

        // then
        assertAll("updateOrCreateUserModerationRecord with existing record:",
                () -> verify(record, times(1)).setModActionCount(3),
                () -> verify(umrr, times(1)).save(record)
        );
    }

    @Test
    void updateOrCreateUserModerationRecord_NoExistingRecord_CreatesNewAndSaves() {
        // given
        GroupListing listing = buildMockListing(); // getUsers().getUserId() = 1L

        Users user = new Users();
        user.setUserId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(umrr.findByUser(user)).thenReturn(Optional.empty());
        // new UserModerationRecord(user) is created internally with modActionCount = 0

        // when
        moderationService.updateOrCreateUserModerationRecord(listing);

        // then — new record is saved with modActionCount = 1 (0 + 1)
        ArgumentCaptor<UserModerationRecord> captor = ArgumentCaptor.forClass(UserModerationRecord.class);
        verify(umrr, times(1)).save(captor.capture());
        assertEquals(1, captor.getValue().getModActionCount(),
                "Newly created UserModerationRecord should have modActionCount = 1");
    }

    // ─── autoModDeleteListing ────────────────────────────────────────────────────

    @Test
    void autoModDeleteListing_RemovesListingFromUserCollectionAndDeletes() {
        // given
        GroupListing listing = buildMockListing();
        Users owner = mock(Users.class);
        Set<GroupListing> groupListings = new HashSet<>();
        groupListings.add(listing);
        when(owner.getGroupListings()).thenReturn(groupListings);

        // when
        moderationService.autoModDeleteListing(listing, owner);

        // then
        assertAll("autoModDeleteListing side-effects:",
                () -> verify(userRepo, times(1)).save(owner),
                () -> verify(glr, times(1)).delete(listing),
                () -> assertFalse(groupListings.contains(listing),
                        "Listing should be removed from owner's collection")
        );
    }

    // ─── maxModReportedBasis via prepareManualModRemovalRecords ──────────────────

    @Test
    void prepareManualModRemovalRecords_SpamBasis_SetsSpamCountToThreshold() {
        // given
        GroupListing listing = buildMockListing();

        Users mod = new Users();
        mod.setUserId(99L);
        mod.setUsername("modUser");

        ManualModDeleteDto dto = new ManualModDeleteDto();
        dto.setGroupId(1L);
        dto.setReportBasis(1);
        dto.setNote("Spam violation");

        Users owner = new Users();
        owner.setUserId(1L);
        owner.setUsername("TestUser");

        ListingReportBasis spamBasis = mock(ListingReportBasis.class);
        when(spamBasis.getBasisLabel()).thenReturn("Spam");

        ListingArchive archive = mock(ListingArchive.class);
        UserModerationRecord record = mock(UserModerationRecord.class);
        when(record.getModActionCount()).thenReturn(0);
        when(record.getLastModActionTs()).thenReturn(Instant.now());

        when(glr.findById(anyLong())).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(any())).thenReturn(Optional.empty());
        when(mir.save(any(ModerationIssue.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lrbr.findById(any())).thenReturn(Optional.of(spamBasis));
        when(archiveService.archiveListing(any(), any(), any(), any(Users.class))).thenReturn(archive);
        when(mlar.save(any(ModListingAction.class))).thenReturn(mock(ModListingAction.class));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(owner));
        when(umrr.findByUser(any(Users.class))).thenReturn(Optional.of(record));

        // when
        moderationService.prepareManualModRemovalRecords(dto, mod);

        // then — capture the saved ModerationIssue and verify spamCount is maxed out
        ArgumentCaptor<ModerationIssue> issueCaptor = ArgumentCaptor.forClass(ModerationIssue.class);
        verify(mir, atLeast(2)).save(issueCaptor.capture());
        ModerationIssue capturedIssue = issueCaptor.getAllValues().getLast();

        assertEquals(AUTO_MOD_REPORT_THRESHOLD, capturedIssue.getSpamCount(),
                "Spam count should be set to AUTO_MOD_REPORT_THRESHOLD for Spam basis");
    }
}
