package com.sc_fleetfinder.fleets.unit_tests.services.reporting_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportBasisRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.SubmitListingReportDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.mod_services.ModerationService;
import com.sc_fleetfinder.fleets.services.reporting_services.ListingReportingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationConstants.AUTO_MOD_REPORT_THRESHOLD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingReportingServiceImplTest {

    @Mock
    private GroupListingRepository glr;
    @Mock
    private ModerationIssueRepository mir;
    @Mock
    private ModerationService modService;
    @Mock
    private ListingReportBasisRepository lbr;
    @Mock
    private ListingReportRepository lrr;

    @InjectMocks
    private ListingReportingServiceImpl reportingService;

    // ─── Helper: builds a plain SubmitListingReportDto ───────────────────────────

    private SubmitListingReportDto buildDto(long groupId, int reportBasis) {
        SubmitListingReportDto dto = new SubmitListingReportDto();
        dto.setGroupId(groupId);
        dto.setReportBasis(reportBasis);
        return dto;
    }

    // ─── Helper: builds a real ModerationIssue with all counts initialised ───────

    private ModerationIssue buildRealIssue() {
        ModerationIssue issue = new ModerationIssue();
        issue.setReportTotalCount(0);
        issue.setSpamCount(0);
        issue.setHateSpeechCount(0);
        issue.setNsfwCount(0);
        issue.setScamCount(0);
        issue.setOffTopicCount(0);
        issue.setTrollCount(0);
        issue.setDoxxCount(0);
        issue.setCheatCount(0);
        issue.setOtherCount(0);
        issue.setStatus("Pending");
        return issue;
    }

    // ─── generateListingReport — core flow ───────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void generateListingReport_FirstReport_CreatesNewIssueAndReport_ReturnsOk() {
        // given
        GroupListing listing = mock(GroupListing.class);
        ModerationIssue modIssue = buildRealIssue();
        Users reporter = new Users();
        reporter.setUserId(2L);

        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(basis.getBasisLabel()).thenReturn("Spam");

        when(glr.findById(1L)).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(listing)).thenReturn(Optional.empty());
        when(modService.generateModerationIssue(listing, "Pending")).thenReturn(modIssue);
        when(lbr.findById(1)).thenReturn(Optional.of(basis));
        when(lrr.save(any(ListingReport.class))).thenAnswer(inv -> inv.getArgument(0));
        // size=1 → reportTotalCount=1 → below AUTO_MOD threshold
        when(lrr.findByModIssueRef(modIssue)).thenReturn(Set.of(mock(ListingReport.class)));

        // when
        ResponseEntity<?> response = reportingService.generateListingReport(buildDto(1L, 1), reporter);

        // then
        assertAll("generateListingReport first report:",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> {
                    Map<String, String> body = (Map<String, String>) response.getBody();
                    assertNotNull(body);
                    assertTrue(body.containsKey("Report Status:"),
                            "Response body should contain 'Report Status:' key");
                },
                () -> verify(lrr, times(1)).save(any(ListingReport.class))
        );
    }

    @Test
    void generateListingReport_ExistingIssue_ReusesExistingIssue_NeverCallsGenerateModerationIssue() {
        // given
        GroupListing listing = mock(GroupListing.class);
        ModerationIssue existingIssue = buildRealIssue();
        Users reporter = new Users();

        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(basis.getBasisLabel()).thenReturn("Spam");

        when(glr.findById(1L)).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(existingIssue));
        when(lbr.findById(1)).thenReturn(Optional.of(basis));
        when(lrr.save(any(ListingReport.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lrr.findByModIssueRef(existingIssue)).thenReturn(Set.of(mock(ListingReport.class)));

        // when
        reportingService.generateListingReport(buildDto(1L, 1), reporter);

        // then
        verify(modService, never()).generateModerationIssue(any(), any());
    }

    @Test
    void generateListingReport_ListingNotFound_ThrowsResourceNotFoundException() {
        // given
        when(glr.findById(999L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResourceNotFoundException.class,
                () -> reportingService.generateListingReport(buildDto(999L, 1), new Users()));
    }

    @Test
    void generateListingReport_BasisNotFound_ThrowsResourceNotFoundException() {
        // given
        GroupListing listing = mock(GroupListing.class);
        ModerationIssue existingIssue = buildRealIssue();

        when(glr.findById(1L)).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(existingIssue));
        when(lbr.findById(99)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResourceNotFoundException.class,
                () -> reportingService.generateListingReport(buildDto(1L, 99), new Users()));
    }

    // ─── incrementOnReport — per-basis branch coverage ───────────────────────────

    @Test
    void generateListingReport_SpamBasis_IncrementsSpamCount() {
        ModerationIssue issue = buildRealIssue();
        issue.setSpamCount(2);
        runIncrementTest(issue, "Spam");
        assertEquals(3, issue.getSpamCount(), "Spam count should be incremented by 1");
    }

    @Test
    void generateListingReport_HateSpeechBasis_IncrementsHateSpeechCount() {
        ModerationIssue issue = buildRealIssue();
        issue.setHateSpeechCount(1);
        runIncrementTest(issue, "Hate Speech");
        assertEquals(2, issue.getHateSpeechCount(), "Hate speech count should be incremented by 1");
    }

    @Test
    void generateListingReport_NsfwBasis_IncrementsNsfwCount() {
        ModerationIssue issue = buildRealIssue();
        issue.setNsfwCount(0);
        runIncrementTest(issue, "NSFW");
        assertEquals(1, issue.getNsfwCount(), "NSFW count should be incremented by 1");
    }

    @Test
    void generateListingReport_ScamBasis_IncrementsScamCount() {
        ModerationIssue issue = buildRealIssue();
        issue.setScamCount(3);
        runIncrementTest(issue, "Scam/Fraud");
        assertEquals(4, issue.getScamCount(), "Scam count should be incremented by 1");
    }

    @Test
    void generateListingReport_OtherBasis_IncrementsOtherCount() {
        ModerationIssue issue = buildRealIssue();
        issue.setOtherCount(0);
        runIncrementTest(issue, "Other");
        assertEquals(1, issue.getOtherCount(), "Other count should be incremented by 1");
    }

    // Shared setup helper for per-basis increment tests
    private void runIncrementTest(ModerationIssue issue, String basisLabel) {
        GroupListing listing = mock(GroupListing.class);
        Users reporter = new Users();

        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(basis.getBasisLabel()).thenReturn(basisLabel);

        when(glr.findById(1L)).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(issue));
        when(lbr.findById(1)).thenReturn(Optional.of(basis));
        when(lrr.save(any(ListingReport.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lrr.findByModIssueRef(issue)).thenReturn(Set.of(mock(ListingReport.class)));

        reportingService.generateListingReport(buildDto(1L, 1), reporter);
    }

    // ─── checkAutoModThresh ───────────────────────────────────────────────────────

    @Test
    void generateListingReport_BelowThreshold_DoesNotTriggerAutoMod() {
        // given — lrr.findByModIssueRef returns 5 reports → totalCount = 5 < 20
        GroupListing listing = mock(GroupListing.class);
        ModerationIssue issue = buildRealIssue();
        Users reporter = new Users();

        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(basis.getBasisLabel()).thenReturn("Spam");

        when(glr.findById(1L)).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(issue));
        when(lbr.findById(1)).thenReturn(Optional.of(basis));
        when(lrr.save(any(ListingReport.class))).thenAnswer(inv -> inv.getArgument(0));

        Set<ListingReport> fiveReports = buildReportSet(5);
        when(lrr.findByModIssueRef(issue)).thenReturn(fiveReports);

        // when
        reportingService.generateListingReport(buildDto(1L, 1), reporter);

        // then
        verify(modService, never()).prepareAutoModRemovalRecords(any());
    }

    @Test
    void generateListingReport_AtThreshold_TriggersAutoModAndSetsStatusAutoMod() {
        // given — lrr.findByModIssueRef returns 20 reports → totalCount = 20 = AUTO_MOD_REPORT_THRESHOLD
        GroupListing listing = mock(GroupListing.class);
        ModerationIssue issue = buildRealIssue();
        issue.setSpamCount(AUTO_MOD_REPORT_THRESHOLD - 1); // will be incremented to threshold
        Users reporter = new Users();

        ListingReportBasis basis = mock(ListingReportBasis.class);
        when(basis.getBasisLabel()).thenReturn("Spam");

        when(glr.findById(1L)).thenReturn(Optional.of(listing));
        when(mir.findByGroupRef(listing)).thenReturn(Optional.of(issue));
        when(lbr.findById(1)).thenReturn(Optional.of(basis));
        when(lrr.save(any(ListingReport.class))).thenAnswer(inv -> inv.getArgument(0));

        Set<ListingReport> twentyReports = buildReportSet(AUTO_MOD_REPORT_THRESHOLD);
        when(lrr.findByModIssueRef(issue)).thenReturn(twentyReports);

        // when
        reportingService.generateListingReport(buildDto(1L, 1), reporter);

        // then
        assertAll("Auto-mod triggered at threshold:",
                () -> verify(modService, times(1)).prepareAutoModRemovalRecords(issue),
                () -> assertEquals("AutoMod", issue.getStatus(),
                        "Issue status should be set to 'AutoMod' when threshold is reached")
        );
    }

    // ─── getUsersReportBrief ──────────────────────────────────────────────────────

    @Test
    void getUsersReportBrief_DelegatesToRepo_ReturnsReportedGroupIds() {
        // given
        Users user = new Users();
        user.setUserId(1L);
        Set<Long> expectedIds = Set.of(1L, 2L);
        when(lrr.findByReportingUserRef(user)).thenReturn(expectedIds);

        // when
        Set<Long> result = reportingService.getUsersReportBrief(user);

        // then
        assertEquals(expectedIds, result, "getUsersReportBrief should delegate to lrr and return the result");
    }

    // ─── Private helpers ──────────────────────────────────────────────────────────

    private Set<ListingReport> buildReportSet(int count) {
        Set<ListingReport> reports = new HashSet<>();
        for (int i = 0; i < count; i++) {
            reports.add(mock(ListingReport.class));
        }
        return reports;
    }
}
