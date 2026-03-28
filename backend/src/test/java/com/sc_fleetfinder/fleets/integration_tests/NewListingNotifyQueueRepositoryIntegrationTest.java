package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.NewListingNotifyQueueRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.NewListingNotifyQueue;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class NewListingNotifyQueueRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private NewListingNotifyQueueRepository queueRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    // Inserts a user and returns the generated id_user.
    private Long insertUser(boolean withDiscord, boolean externalGroupNotesEnabled) {
        String uuid = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO users (keycloak_id, user_name, email, external_sys_notes_enabled, " +
                "external_group_notes_enabled, external_social_notes_enabled, discord_user_id) " +
                "VALUES (?, ?, ?, 0, ?, 0, ?)",
                uuid,
                "user_" + uuid.substring(0, 8),
                uuid.substring(0, 8) + "@test.com",
                externalGroupNotesEnabled ? 1 : 0,
                withDiscord ? "123456789012345678" : null
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_user) FROM users", Long.class);
    }

    // Inserts a group_listing owned by the given user and returns its id_group.
    private Long insertListing(Long ownerId) {
        jdbcTemplate.update(
                "INSERT INTO group_listing (id_user, server_id, environment_id, experience_id, " +
                "listing_title, legality_id, group_status_id, category_id, pvp_status_id, system_id, " +
                "current_party_size, desired_party_size, comms_options, language_code, " +
                "vis_status, last_updated, listing_description) " +
                "VALUES (?, 1, 1, 1, 'Queue test listing', 1, 1, 1, 1, 1, " +
                "1, 2, 'Optional', 'English', 'FRESH', NOW(), 'Test description.')",
                ownerId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_group) FROM group_listing", Long.class);
    }

    // Inserts a queue entry with the given status and a NULL locked_at.
    private void insertQueueEntry(Long listingId, String status) {
        jdbcTemplate.update(
                "INSERT INTO new_listing_notify_queue (id_group, status, queued_at) VALUES (?, ?, NOW())",
                listingId, status
        );
    }

    // Inserts a queue entry with an explicit locked_at SQL expression.
    private void insertQueueEntryWithLock(Long listingId, String status, String lockedAtExpr) {
        jdbcTemplate.update(
                "INSERT INTO new_listing_notify_queue (id_group, status, queued_at, locked_at) " +
                "VALUES (?, ?, NOW(), " + lockedAtExpr + ")",
                listingId, status
        );
    }

    // Inserts a queue entry whose processed_at is set via SQL expression.
    private void insertProcessedQueueEntry(Long listingId, String processedAtExpr) {
        jdbcTemplate.update(
                "INSERT INTO new_listing_notify_queue (id_group, status, queued_at, processed_at) " +
                "VALUES (?, 'PROCESSED', NOW(), " + processedAtExpr + ")",
                listingId
        );
    }

    // Inserts a user_custom_notification with all FK filters NULL (wildcard match).
    // languageCode may be set to test filter matching; pass null for wildcard.
    private void insertCustomNote(Long receiverId, String languageCode) {
        jdbcTemplate.update(
                "INSERT INTO user_custom_notification (user_id, enabled, tag_label, language_code) " +
                "VALUES (?, 1, 'Test Filter', ?)",
                receiverId, languageCode
        );
    }

    // Inserts a disabled custom notification (enabled = 0).
    private void insertDisabledCustomNote(Long receiverId) {
        jdbcTemplate.update(
                "INSERT INTO user_custom_notification (user_id, enabled, tag_label) VALUES (?, 0, 'Disabled')",
                receiverId
        );
    }

    // Inserts a push_subscription for the given user with specific group_notes_enabled.
    private void insertPushSubscription(Long userId, boolean groupNotesEnabled) {
        jdbcTemplate.update(
                "INSERT INTO push_subscription (user_id, user_label, device_url, public_key, browser_secret, group_notes_enabled) " +
                "VALUES (?, 'Test Device', 'https://push.example.com/token', 'pk123', 'sec123', ?)",
                userId, groupNotesEnabled ? 1 : 0
        );
    }

    // ─── claimForProcessing() ────────────────────────────────────────────────

    @Test
    void claimForProcessing_InQueueItem_ChangesStatusToClaimed() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "IN_QUEUE");

        int claimed = queueRepo.claimForProcessing(10);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM new_listing_notify_queue WHERE id_group = ?", String.class, listingId);
        assertAll(
                () -> assertTrue(claimed >= 1, "Expected at least 1 row claimed"),
                () -> assertEquals("CLAIMED", status)
        );
    }

    @Test
    void claimForProcessing_RespectsLimit_OnlyClaimsSpecifiedCount() {
        Long owner1 = insertUser(false, false);
        Long owner2 = insertUser(false, false);
        Long listing1 = insertListing(owner1);
        Long listing2 = insertListing(owner2);
        insertQueueEntry(listing1, "IN_QUEUE");
        insertQueueEntry(listing2, "IN_QUEUE");

        int claimed = queueRepo.claimForProcessing(1);

        assertEquals(1, claimed, "LIMIT 1 should claim exactly 1 item");
    }

    @Test
    void claimForProcessing_StaleLockedInQueueItem_IsReclaimed() {
        // IN_QUEUE item with a locked_at older than 2 minutes — should be claimable
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntryWithLock(listingId, "IN_QUEUE", "DATE_SUB(NOW(), INTERVAL 5 MINUTE)");

        int claimed = queueRepo.claimForProcessing(10);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM new_listing_notify_queue WHERE id_group = ?", String.class, listingId);
        assertAll(
                () -> assertTrue(claimed >= 1),
                () -> assertEquals("CLAIMED", status)
        );
    }

    @Test
    void claimForProcessing_FreshLockedInQueueItem_IsNotClaimed() {
        // IN_QUEUE item with a very recent locked_at — should not be claimed
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntryWithLock(listingId, "IN_QUEUE", "DATE_SUB(NOW(), INTERVAL 30 SECOND)");

        queueRepo.claimForProcessing(10);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM new_listing_notify_queue WHERE id_group = ?", String.class, listingId);
        assertEquals("IN_QUEUE", status, "Freshly locked IN_QUEUE item should not be reclaimed");
    }

    // ─── findClaimedItems() ──────────────────────────────────────────────────

    @Test
    void findClaimedItems_ReturnsRecentlyClaimedItems() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        // Insert CLAIMED with a fresh lock (within 2 minutes)
        insertQueueEntryWithLock(listingId, "CLAIMED", "DATE_SUB(NOW(), INTERVAL 30 SECOND)");

        List<NewListingNotifyQueue> result = queueRepo.findClaimedItems(10);

        assertTrue(result.stream().anyMatch(q -> q.getGroupId().equals(listingId)),
                "Recently claimed item should be returned by findClaimedItems");
    }

    @Test
    void findClaimedItems_ExcludesStaleLockedItems() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        // Insert CLAIMED with a stale lock (over 2 minutes old)
        insertQueueEntryWithLock(listingId, "CLAIMED", "DATE_SUB(NOW(), INTERVAL 5 MINUTE)");

        List<NewListingNotifyQueue> result = queueRepo.findClaimedItems(10);

        assertTrue(result.stream().noneMatch(q -> q.getGroupId().equals(listingId)),
                "Stale CLAIMED item should NOT be returned by findClaimedItems");
    }

    // ─── markProcessed() ────────────────────────────────────────────────────

    @Test
    void markProcessed_SetsProcessedStatus_ForStaleClaimedEntries() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        // CLAIMED with a stale lock qualifies for markProcessed
        insertQueueEntryWithLock(listingId, "CLAIMED", "DATE_SUB(NOW(), INTERVAL 5 MINUTE)");

        int updated = queueRepo.markProcessed();

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM new_listing_notify_queue WHERE id_group = ?", String.class, listingId);
        assertAll(
                () -> assertTrue(updated >= 1, "Expected at least 1 row processed"),
                () -> assertEquals("PROCESSED", status)
        );
    }

    @Test
    void markProcessed_DoesNotProcess_FreshlyClaimedEntries() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        // CLAIMED with a fresh lock should NOT be marked processed
        insertQueueEntryWithLock(listingId, "CLAIMED", "DATE_SUB(NOW(), INTERVAL 30 SECOND)");

        queueRepo.markProcessed();

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM new_listing_notify_queue WHERE id_group = ?", String.class, listingId);
        assertEquals("CLAIMED", status, "Freshly claimed item should not be marked PROCESSED");
    }

    // ─── deleteOldProcessedEntries() ─────────────────────────────────────────

    @Test
    void deleteOldProcessedEntries_DeletesEntriesOlderThanThreeHours() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertProcessedQueueEntry(listingId, "DATE_SUB(NOW(), INTERVAL 4 HOUR)");

        int deleted = queueRepo.deleteOldProcessedEntries();

        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM new_listing_notify_queue WHERE id_group = ?", Integer.class, listingId);
        assertAll(
                () -> assertTrue(deleted >= 1, "Expected at least 1 entry deleted"),
                () -> assertEquals(0, remaining, "Old PROCESSED entry should be deleted")
        );
    }

    @Test
    void deleteOldProcessedEntries_PreservesRecentProcessedEntries() {
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertProcessedQueueEntry(listingId, "DATE_SUB(NOW(), INTERVAL 30 MINUTE)");

        queueRepo.deleteOldProcessedEntries();

        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM new_listing_notify_queue WHERE id_group = ?", Integer.class, listingId);
        assertEquals(1, remaining, "Recent PROCESSED entry should not be deleted");
    }

    // ─── generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches() ─

    @Test
    void generateOutbox_BasicMatch_CreatesInAppEntry() {
        // given: listing in queue (CLAIMED), receiver with a wildcard custom note (all NULL filters)
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);  // null = wildcard

        int inserted = queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        String channel = jdbcTemplate.queryForObject(
                "SELECT delivery_channel FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                String.class, listingId);
        assertAll(
                () -> assertTrue(inserted >= 1),
                () -> assertEquals(1, outboxCount, "Basic match should create exactly 1 IN_APP entry"),
                () -> assertEquals("IN_APP", channel)
        );
    }

    @Test
    void generateOutbox_UserWithDiscordAndGroupNotesEnabled_CreatesDiscordEntry() {
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(true, true);  // discord + external_group_notes_enabled
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH' AND delivery_channel = 'DISCORD'",
                Integer.class, listingId);
        assertAll(
                () -> assertEquals(2, count, "Discord-enabled receiver should get IN_APP + DISCORD"),
                () -> assertEquals(1, discordCount)
        );
    }

    @Test
    void generateOutbox_UserWithDiscordId_GroupNotesDisabled_NoDiscordEntry() {
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(true, false);  // discord_id, but external_group_notes_enabled = 0
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH' AND delivery_channel = 'DISCORD'",
                Integer.class, listingId);
        assertEquals(0, discordCount, "No DISCORD entry when external_group_notes_enabled = 0");
    }

    @Test
    void generateOutbox_UserWithPushGroupNotesEnabled_CreatesPushEntry() {
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);
        insertPushSubscription(receiverId, true);  // group_notes_enabled = 1

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH' AND delivery_channel = 'PUSH'",
                Integer.class, listingId);
        assertAll(
                () -> assertEquals(2, count, "Push (group_notes_enabled=1) → IN_APP + PUSH"),
                () -> assertEquals(1, pushCount)
        );
    }

    @Test
    void generateOutbox_UserWithPushGroupNotesDisabled_NoPushEntry() {
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);
        insertPushSubscription(receiverId, false);  // group_notes_enabled = 0

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH' AND delivery_channel = 'PUSH'",
                Integer.class, listingId);
        assertEquals(0, pushCount, "No PUSH entry when push.group_notes_enabled = 0");
    }

    @Test
    void generateOutbox_UserWithMultiplePushSubs_ProducesMultipleEntries() {
        // Two push subscriptions both with group_notes_enabled=1; on duplicate key should allow multiple push sub notes
        // for a single event
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);
        insertPushSubscription(receiverId, true);
        insertPushSubscription(receiverId, true);

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH' AND delivery_channel = 'PUSH'",
                Integer.class, listingId);
        assertAll(
                () -> assertEquals(3, totalCount, "2 push subs → 1 IN_APP + 2 PUSH"),
                () -> assertEquals(2, pushCount, "Exactly 2 PUSH entries when 2 subscriptions are active")
        );
    }

    @Test
    void generateOutbox_CustomNoteOwnerIsListingOwner_Excluded() {
        // Receiver has a custom note but they own the listing — excluded by customNote.user_id != gl.id_user
        Long ownerId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(ownerId, null);  // same user is owner

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        assertEquals(0, outboxCount, "Listing owner should not receive a notification for their own listing");
    }

    @Test
    void generateOutbox_DisabledCustomNote_NoEntry() {
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertDisabledCustomNote(receiverId);  // enabled = 0

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        assertEquals(0, outboxCount, "Disabled custom note should not generate any outbox entries");
    }

    @Test
    void generateOutbox_LanguageCodeMismatch_NoEntry() {
        // Listing uses language_code='English'; custom note filters on 'Spanish' → no match
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, "Spanish");

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        assertEquals(0, outboxCount, "Language code mismatch should produce no outbox entry");
    }

    @Test
    void generateOutbox_NullLanguageCodeFilter_MatchesAnyLanguage() {
        // Custom note with null language_code = wildcard, matches listing's 'English'
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "CLAIMED");
        insertCustomNote(receiverId, null);  // null = wildcard

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        assertTrue(outboxCount >= 1, "NULL language filter should wildcard-match any listing language");
    }

    @Test
    void generateOutbox_QueueStatusNotClaimed_Excluded() {
        // Queue entry with status='IN_QUEUE' should not be processed (only CLAIMED)
        Long ownerId = insertUser(false, false);
        Long receiverId = insertUser(false, false);
        Long listingId = insertListing(ownerId);
        insertQueueEntry(listingId, "IN_QUEUE");
        insertCustomNote(receiverId, null);

        queueRepo.generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();

        Integer outboxCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_LISTING_MATCH'",
                Integer.class, listingId);
        assertEquals(0, outboxCount, "IN_QUEUE entries should not generate outbox entries (only CLAIMED)");
    }
}
