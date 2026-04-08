package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class ModerationIssueRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private ModerationIssueRepository mir;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Long insertListing() {
        jdbcTemplate.update(
                "INSERT INTO group_listing (id_user, server_id, environment_id, experience_id, " +
                "listing_title, legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, listing_description) " +
                "VALUES (1, 1, 1, 1, 'Mod issue test', 1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', 'English', 'FRESH', NOW(), 'Test description.')"
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);
    }

    private Long insertIssue(Long groupId) {
        jdbcTemplate.update(
                "INSERT INTO moderation_issue (id_group, id_user, status) VALUES (?, 1, 'Pending')",
                groupId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_issue) FROM moderation_issue", Long.class);
    }

    private Long insertReporter() {
        String uuid = java.util.UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO users (keycloak_id, user_name, email, in_game_username) VALUES (?, ?, ?, ?)",
                uuid, "reporter_" + uuid.substring(0, 8), uuid.substring(0, 8) + "@test.com", "inGameUsername"
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_user) FROM users", Long.class);
    }

    // reporterId must be unique per (id_group, id_reporter) UNIQUE constraint
    private void insertReport(Long issueId, Long groupId, int basisId, Long reporterId) {
        jdbcTemplate.update(
                "INSERT INTO listing_report (id_issue, id_group, id_reporter, id_basis) VALUES (?, ?, ?, ?)",
                issueId, groupId, reporterId, basisId
        );
    }

    // ─── findAutoModActionBasis_MostCommonReportBasis ─────────────────────────

    @Test
    void findAutoModActionBasis_MultipleReports_ReturnsMostCommonBasis() {
        // given: 3 Spam (id=1) reports and 1 Hate Speech (id=2) report — Spam is the plurality
        Long groupId = insertListing();
        Long issueId = insertIssue(groupId);
        insertReport(issueId, groupId, 1, insertReporter());  // Spam
        insertReport(issueId, groupId, 1, insertReporter());  // Spam
        insertReport(issueId, groupId, 1, insertReporter());  // Spam
        insertReport(issueId, groupId, 2, insertReporter());  // Hate Speech

        // when
        ListingReportBasis result = mir.findAutoModActionBasis_MostCommonReportBasis(issueId);

        // then
        assertAll("Most common basis should be Spam:",
                () -> assertNotNull(result, "Result should not be null when reports exist"),
                () -> assertEquals("Spam", result.getBasisLabel(),
                        "Most common basis label should be 'Spam'")
        );
    }

    @Test
    void findAutoModActionBasis_NoReports_ReturnsNull() {
        // given: issue with no listing_report rows
        Long groupId = insertListing();
        Long issueId = insertIssue(groupId);

        // when
        ListingReportBasis result = mir.findAutoModActionBasis_MostCommonReportBasis(issueId);

        // then
        assertNull(result, "Query should return null when no reports exist for the issue");
    }
}
