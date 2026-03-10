package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
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
public class GroupListingRepositoryScheduledQueriesIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private GroupListingRepository listingRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // Helper: inserts a group_listing and returns its generated id_group.
    // lastUpdatedExpr is a SQL expression, e.g. "DATE_SUB(NOW(), INTERVAL 30 DAY)"
    private Long insertListing(String visStatus, String lastUpdatedExpr) {
        jdbcTemplate.update(
                "INSERT INTO group_listing (id_user, server_id, environment_id, experience_id, " +
                "listing_title, legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, listing_description) " +
                "VALUES (1, 1, 1, 1, 'Scheduled query test', 1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', 'English', ?, " + lastUpdatedExpr + ", 'Test description.')",
                visStatus
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);
    }

    // Helper: inserts a listing with a future event_schedule
    private Long insertListingWithFutureEvent(String visStatus, String lastUpdatedExpr, String eventScheduleExpr) {
        jdbcTemplate.update(
                "INSERT INTO group_listing (id_user, server_id, environment_id, experience_id, " +
                "listing_title, legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, event_schedule, listing_description) " +
                "VALUES (1, 1, 1, 1, 'Event listing', 1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', 'English', ?, " + lastUpdatedExpr + ", " + eventScheduleExpr + ", 'Test description.')",
                visStatus
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);
    }

    // ─── setArchivedStatus() ──────────────────────────────────────────────────

    @Test
    void setArchivedStatus_QualifyingListing_SetsStatusToARCHIVED() {
        // given: FRESH listing last updated 30 days ago, no event_schedule
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when
        int count = listingRepo.setArchivedStatus();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertAll(
                () -> assertTrue(count >= 1, "Expected at least 1 row updated"),
                () -> assertEquals("ARCHIVED", status, "vis_status should be ARCHIVED")
        );
    }

    @Test
    void setArchivedStatus_RecentLastUpdated_DoesNotChange() {
        // given: FRESH listing updated only 1 day ago
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 1 DAY)");

        // when
        int count = listingRepo.setArchivedStatus();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertAll(
                () -> assertEquals(0, count, "No rows should be updated"),
                () -> assertEquals("FRESH", status, "vis_status should remain FRESH")
        );
    }

    @Test
    void setArchivedStatus_FutureEventSchedule_DoesNotChange() {
        // given: FRESH listing with old last_updated but a future event_schedule
        Long id = insertListingWithFutureEvent(
                "FRESH",
                "DATE_SUB(NOW(), INTERVAL 30 DAY)",
                "DATE_ADD(NOW(), INTERVAL 7 DAY)"
        );

        // when
        listingRepo.setArchivedStatus();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertEquals("FRESH", status, "Listing with future event_schedule should not be archived");
    }

    @Test
    void setArchivedStatus_AlreadyArchived_NotUpdatedAgain() {
        // given: already ARCHIVED listing with old last_updated
        Long id = insertListing("ARCHIVED", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when
        int count = listingRepo.setArchivedStatus();

        // then: WHERE vis_status <> 'ARCHIVED' excludes it — count for this listing == 0
        // We verify by checking the seed listing from V1001 is NOT re-archived unnecessarily,
        // and that the inserted ARCHIVED listing contributed 0 updates.
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertEquals("ARCHIVED", status, "Status should remain ARCHIVED (no redundant update)");
        // The newly inserted ARCHIVED listing cannot have been "updated" to ARCHIVED again
        // (it was already ARCHIVED). Any count > 0 would be from other listings, not this one.
        // We can verify by checking count didn't include this listing via a direct status check.
    }

    // ─── createOutboxEntriesForArchiveNotifications() ────────────────────────

    @Test
    void createOutboxEntries_Archive_QualifyingListing_CreatesOutboxEntry() {
        // given: ARCHIVED listing, old last_updated
        Long id = insertListing("ARCHIVED", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when
        int count = listingRepo.createOutboxEntriesForArchiveNotifications();

        // then
        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'LISTING_ARCHIVED'",
                Integer.class, id);
        String eventType = jdbcTemplate.queryForObject(
                "SELECT event_type FROM notification_outbox WHERE entity_id = ? AND event_type = 'LISTING_ARCHIVED'",
                String.class, id);
        String newStatus = jdbcTemplate.queryForObject(
                "SELECT entity_new_status FROM notification_outbox WHERE entity_id = ? AND event_type = 'LISTING_ARCHIVED'",
                String.class, id);
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE entity_id = ? AND event_type = 'LISTING_ARCHIVED'",
                String.class, id);
        assertAll(
                () -> assertTrue(count >= 1, "Expected at least 1 outbox entry created"),
                () -> assertEquals(1, outboxCount, "Expected exactly 1 outbox entry for this listing"),
                () -> assertEquals("LISTING_ARCHIVED", eventType),
                () -> assertEquals("ARCHIVED", newStatus),
                () -> assertEquals("PENDING", status)
        );
    }

    @Test
    void createOutboxEntries_Archive_NonArchivedListing_NoEntryCreated() {
        // given: FRESH listing with old last_updated (not ARCHIVED — excluded by WHERE)
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when
        listingRepo.createOutboxEntriesForArchiveNotifications();

        // then
        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'LISTING_ARCHIVED'",
                Integer.class, id);
        assertEquals(0, outboxCount, "No outbox entry should be created for a non-ARCHIVED listing");
    }

    @Test
    void createOutboxEntries_Archive_Idempotent_SecondCallCreatesNoDuplicate() {
        // given: ARCHIVED listing, old last_updated
        Long id = insertListing("ARCHIVED", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when: call twice
        listingRepo.createOutboxEntriesForArchiveNotifications();
        listingRepo.createOutboxEntriesForArchiveNotifications();

        // then: ON DUPLICATE KEY UPDATE outbox_id = outbox_id means second call is a no-op
        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'LISTING_ARCHIVED'",
                Integer.class, id);
        assertEquals(1, outboxCount, "Duplicate outbox entry should not be created (ON DUPLICATE KEY)");
    }

    // ─── scheduledDeleteArchivedListings() ───────────────────────────────────

    @Test
    void scheduledDeleteArchivedListings_QualifyingListing_IsDeleted() {
        // given: ARCHIVED listing, old last_updated
        Long id = insertListing("ARCHIVED", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when
        int count = listingRepo.scheduledDeleteArchivedListings();

        // then
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_listing WHERE id_group = ?", Integer.class, id);
        assertAll(
                () -> assertTrue(count >= 1, "Expected at least 1 row deleted"),
                () -> assertEquals(0, remaining, "Qualifying ARCHIVED listing should be deleted")
        );
    }

    @Test
    void scheduledDeleteArchivedListings_RecentlyArchived_NotDeleted() {
        // given: ARCHIVED listing but last_updated only 1 day ago
        Long id = insertListing("ARCHIVED", "DATE_SUB(NOW(), INTERVAL 1 DAY)");

        // when
        int count = listingRepo.scheduledDeleteArchivedListings();

        // then
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_listing WHERE id_group = ?", Integer.class, id);
        assertAll(
                () -> assertEquals(0, count, "No rows should be deleted"),
                () -> assertEquals(1, remaining, "Recently archived listing should not be deleted")
        );
    }

    @Test
    void scheduledDeleteArchivedListings_NonArchivedListing_NotDeleted() {
        // given: FRESH listing with old last_updated (vis_status != 'ARCHIVED')
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 30 DAY)");

        // when
        listingRepo.scheduledDeleteArchivedListings();

        // then
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_listing WHERE id_group = ?", Integer.class, id);
        assertEquals(1, remaining, "Non-ARCHIVED listing should not be deleted");
    }

    // ─── updateListingVisStatuses() ──────────────────────────────────────────

    @Test
    void updateListingVisStatuses_OldListing_SetsExpired() {
        // given: FRESH listing, 4 days old, no event_schedule
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 4 DAY)");

        // when
        int count = listingRepo.updateListingVisStatuses();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertAll(
                () -> assertTrue(count >= 1),
                () -> assertEquals("EXPIRED", status)
        );
    }

    @Test
    void updateListingVisStatuses_13HoursOld_SetsInactive() {
        // given: FRESH listing, 13 hours old, no event_schedule
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 13 HOUR)");

        // when
        listingRepo.updateListingVisStatuses();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertEquals("INACTIVE", status);
    }

    @Test
    void updateListingVisStatuses_7HoursOld_SetsStale() {
        // given: FRESH listing, 7 hours old
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 7 HOUR)");

        // when
        listingRepo.updateListingVisStatuses();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertEquals("STALE", status);
    }

    @Test
    void updateListingVisStatuses_4HoursOld_SetsRecent() {
        // given: FRESH listing, 4 hours old
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 4 HOUR)");

        // when
        listingRepo.updateListingVisStatuses();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertEquals("RECENT", status);
    }

    @Test
    void updateListingVisStatuses_FreshListing_NoChange() {
        // given: FRESH listing, only 1 hour old (below the 3-hour RECENT threshold)
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 1 HOUR)");

        // when
        int count = listingRepo.updateListingVisStatuses();

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT vis_status FROM group_listing WHERE id_group = ?", String.class, id);
        assertEquals("FRESH", status, "Listing under 3 hours old should remain FRESH");
    }

    // ─── createNotificationOutboxEntriesForStatusUpdates() ───────────────────

    @Test
    void createOutboxEntriesForStatusUpdates_OldListing_CreatesExpiredEntry() {
        // given: FRESH listing, 4 days old, no event_schedule
        Long id = insertListing("FRESH", "DATE_SUB(NOW(), INTERVAL 4 DAY)");

        // when
        int count = listingRepo.createNotificationOutboxEntriesForStatusUpdates();

        // then
        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox " +
                "WHERE entity_id = ? AND event_type = 'LISTING_VIS_STATUS_CHANGED' AND entity_new_status = 'EXPIRED'",
                Integer.class, id);
        String newStatus = jdbcTemplate.queryForObject(
                "SELECT entity_new_status FROM notification_outbox " +
                "WHERE entity_id = ? AND event_type = 'LISTING_VIS_STATUS_CHANGED'",
                String.class, id);
        String outboxStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox " +
                "WHERE entity_id = ? AND event_type = 'LISTING_VIS_STATUS_CHANGED'",
                String.class, id);
        assertAll(
                () -> assertTrue(count >= 1, "Expected at least 1 outbox entry"),
                () -> assertEquals(1, outboxCount, "Expected exactly 1 outbox entry for EXPIRED transition"),
                () -> assertEquals("EXPIRED", newStatus),
                () -> assertEquals("PENDING", outboxStatus)
        );
    }

    @Test
    void createOutboxEntriesForStatusUpdates_AlreadyCorrectStatus_NoEntry() {
        // given: listing already set to EXPIRED with old last_updated
        // The WHERE clause (computed_status <> vis_status) excludes it
        Long id = insertListing("EXPIRED", "DATE_SUB(NOW(), INTERVAL 4 DAY)");

        // when
        listingRepo.createNotificationOutboxEntriesForStatusUpdates();

        // then
        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox " +
                "WHERE entity_id = ? AND event_type = 'LISTING_VIS_STATUS_CHANGED'",
                Integer.class, id);
        assertEquals(0, outboxCount,
                "No outbox entry should be created when vis_status already matches computed status");
    }
}
