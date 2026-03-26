package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class NotificationRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final String TEST_SIBLING_KEY = "a".repeat(64);

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Long insertUser(boolean withDiscord, boolean sysNotesEnabled) {
        String uuid = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO users (keycloak_id, user_name, email, external_sys_notes_enabled, " +
                "external_group_notes_enabled, external_social_notes_enabled, discord_user_id) " +
                "VALUES (?, ?, ?, ?, 0, 0, ?)",
                uuid,
                "user_" + uuid.substring(0, 8),
                uuid.substring(0, 8) + "@test.com",
                sysNotesEnabled ? 1 : 0,
                withDiscord ? "123456789012345678" : null
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_user) FROM users", Long.class);
    }

    // Inserts a listing_archive row for the given owner. All reference-data FK columns
    // use seed IDs (1) from V2. id_group is set to ownerId * 1000 to satisfy the UNIQUE constraint
    // within a single test (auto-rollback ensures no cross-test collisions).
    private Long insertListingArchive(Long ownerId) {
        jdbcTemplate.update(
                "INSERT INTO listing_archive " +
                "(id_group, id_user, username, listing_title, listing_description, " +
                "server_id, environment_id, experience_id, legality_id, group_status_id, " +
                "category_id, pvp_status_id, system_id, current_party_size, desired_party_size, " +
                "comms_options, language_code, listing_creation_ts, listing_last_updated) " +
                "VALUES (?, ?, 'testuser', 'Test Archived Listing', 'A description.', " +
                "1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 'Optional', 'English', NOW(), NOW())",
                ownerId * 1000, ownerId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_archive) FROM listing_archive", Long.class);
    }

    // Inserts a mod_listing_action referencing the given archive entry and listing owner.
    private Long insertModAction(Long archiveId, Long ownerId) {
        jdbcTemplate.update(
                "INSERT INTO mod_listing_action (id_archive, id_user, username, action_type, action_note) " +
                "VALUES (?, ?, 'testuser', 'Manual', 'Test mod delete action')",
                archiveId, ownerId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_action) FROM mod_listing_action", Long.class);
    }

    private void insertPushSubscription(Long userId, boolean sysNotesEnabled) {
        jdbcTemplate.update(
                "INSERT INTO push_subscription (user_id, user_label, device_url, public_key, browser_secret, sys_notes_enabled) " +
                "VALUES (?, 'Test Device', 'https://push.example.com/token', 'pk123', 'sec123', ?)",
                userId, sysNotesEnabled ? 1 : 0
        );
    }

    // readAtExpr is a raw SQL expression: "NOW()" for read, "NULL" for unread.
    private Long insertNotification(Long userId, String siblingKey, String deliveryChannel, String readAtExpr) {
        jdbcTemplate.update(
                "INSERT INTO notification (id_user, type, message, delivery_channel, sibling_key, read_at) " +
                "VALUES (?, 'LISTING_VIS_STATUS_CHANGED', 'Test notification.', ?, ?, " + readAtExpr + ")",
                userId, deliveryChannel, siblingKey
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_notification) FROM notification", Long.class);
    }

    // Inserts a notification with explicit dropdown_priority and no sibling_key.
    // readAtExpr is a raw SQL expression: "NOW()" for read, "NULL" for unread.
    private Long insertNotification(Long userId, String deliveryChannel, boolean dropdownPriority, String readAtExpr) {
        jdbcTemplate.update(
                "INSERT INTO notification (id_user, type, message, delivery_channel, dropdown_priority, read_at) " +
                "VALUES (?, 'LISTING_VIS_STATUS_CHANGED', 'Test notification.', ?, ?, " + readAtExpr + ")",
                userId, deliveryChannel, dropdownPriority ? 1 : 0
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_notification) FROM notification", Long.class);
    }

    // ─── generateOutboxNotificationsOnModListingDelete() ─────────────────────

    @Test
    void generateOutbox_ModDelete_DefaultUser_CreatesInAppEntry() {
        // given: user with no discord and no push subscriptions
        Long ownerId = insertUser(false, false);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        int inserted = notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        String channel = jdbcTemplate.queryForObject(
                "SELECT delivery_channel FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                String.class, actionId);
        String eventType = jdbcTemplate.queryForObject(
                "SELECT event_type FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                String.class, actionId);
        String entityType = jdbcTemplate.queryForObject(
                "SELECT entity_type FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                String.class, actionId);
        String entityNewStatus = jdbcTemplate.queryForObject(
                "SELECT entity_new_status FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                String.class, actionId);
        String parentEntityType = jdbcTemplate.queryForObject(
                "SELECT parent_entity_type FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                String.class, actionId);
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                String.class, actionId);
        assertAll(
                () -> assertTrue(inserted >= 1, "Expected at least 1 outbox entry created"),
                () -> assertEquals(1, count, "Default user (no discord, no push) should get exactly 1 IN_APP entry"),
                () -> assertEquals("IN_APP", channel),
                () -> assertEquals("MOD_DELETE", eventType),
                () -> assertEquals("LISTING_ARCHIVE", entityType),
                () -> assertEquals("ARCHIVED", entityNewStatus),
                () -> assertEquals("MOD_LISTING_ACTION", parentEntityType),
                () -> assertEquals("PENDING", status)
        );
    }

    @Test
    void generateOutbox_ModDelete_DiscordSysNotesEnabled_CreatesInAppAndDiscordEntries() {
        // given: user with discord_user_id and external_sys_notes_enabled = 1
        Long ownerId = insertUser(true, true);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE' AND delivery_channel = 'DISCORD'",
                Integer.class, actionId);
        assertAll(
                () -> assertEquals(2, totalCount, "Discord-enabled user should get IN_APP + DISCORD entries"),
                () -> assertEquals(1, discordCount)
        );
    }

    @Test
    void generateOutbox_ModDelete_DiscordSysNotesDisabled_NoDiscordEntry() {
        // given: user has discord_user_id but external_sys_notes_enabled = 0
        Long ownerId = insertUser(true, false);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE' AND delivery_channel = 'DISCORD'",
                Integer.class, actionId);
        assertAll(
                () -> assertEquals(1, totalCount, "Discord with sys_notes disabled should get only IN_APP"),
                () -> assertEquals(0, discordCount, "No DISCORD entry when external_sys_notes_enabled = 0")
        );
    }

    @Test
    void generateOutbox_ModDelete_PushSysNotesEnabled_CreatesInAppAndPushEntries() {
        // given: user with a push_subscription where sys_notes_enabled = 1
        Long ownerId = insertUser(false, false);
        insertPushSubscription(ownerId, true);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE' AND delivery_channel = 'PUSH'",
                Integer.class, actionId);
        assertAll(
                () -> assertEquals(2, totalCount, "Push (sys_notes_enabled=1) should get IN_APP + PUSH"),
                () -> assertEquals(1, pushCount)
        );
    }

    @Test
    void generateOutbox_ModDelete_PushSysNotesDisabled_NoPushEntry() {
        // given: user has a push_subscription but sys_notes_enabled = 0
        Long ownerId = insertUser(false, false);
        insertPushSubscription(ownerId, false);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE' AND delivery_channel = 'PUSH'",
                Integer.class, actionId);
        assertAll(
                () -> assertEquals(1, totalCount, "Push with sys_notes disabled should get only IN_APP"),
                () -> assertEquals(0, pushCount, "No PUSH entry when push.sys_notes_enabled = 0")
        );
    }

    @Test
    void generateOutbox_ModDelete_MultiplePushSubs_OnlyOnePushEntry() {
        // given: 2 push subscriptions both with sys_notes_enabled = 1
        // ON DUPLICATE KEY UPDATE should deduplicate to exactly 1 PUSH outbox entry
        Long ownerId = insertUser(false, false);
        insertPushSubscription(ownerId, true);
        insertPushSubscription(ownerId, true);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE' AND delivery_channel = 'PUSH'",
                Integer.class, actionId);
        assertAll(
                () -> assertEquals(2, totalCount, "2 push subs should still yield 2 total entries (1 IN_APP + 1 PUSH deduped)"),
                () -> assertEquals(1, pushCount, "Exactly 1 PUSH entry despite 2 subscriptions (ON DUPLICATE KEY)")
        );
    }

    @Test
    void generateOutbox_ModDelete_Idempotent_SecondCallIsNoop() {
        // given: second call with the same actionId should not create duplicate rows
        Long ownerId = insertUser(false, false);
        Long archiveId = insertListingArchive(ownerId);
        Long actionId = insertModAction(archiveId, ownerId);

        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);
        notificationRepository.generateOutboxNotificationsOnModListingDelete(actionId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE parent_entity_id = ? AND event_type = 'MOD_DELETE'",
                Integer.class, actionId);
        assertEquals(1, count, "Second call must not create duplicates (ON DUPLICATE KEY)");
    }

    @Test
    void generateOutbox_ModDelete_WrongActionId_NoEntry() {
        // given: a non-existent action ID — WHERE clause produces no matching rows
        Long ownerId = insertUser(false, false);
        Long archiveId = insertListingArchive(ownerId);
        insertModAction(archiveId, ownerId);
        Long nonExistentActionId = Long.MAX_VALUE;

        int inserted = notificationRepository.generateOutboxNotificationsOnModListingDelete(nonExistentActionId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE event_type = 'MOD_DELETE' AND parent_entity_id = ?",
                Integer.class, nonExistentActionId);
        assertAll(
                () -> assertEquals(0, inserted, "Non-existent action ID should insert 0 rows"),
                () -> assertEquals(0, count)
        );
    }

    // ─── checkSiblingNotificationReadStatus() ────────────────────────────────

    @Test
    void checkSiblingNotificationReadStatus_InAppRead_ReturnsReadAtInstant() {
        // given: an IN_APP notification that has been read (read_at = NOW())
        Long userId = insertUser(false, false);
        Long noteId = insertNotification(userId, TEST_SIBLING_KEY, "IN_APP", "NOW()");

        Optional<Instant> result = notificationRepository.checkSiblingNotificationReadStatus(userId, TEST_SIBLING_KEY);

        Long expectedEpoch = jdbcTemplate.queryForObject(
                "SELECT UNIX_TIMESTAMP(read_at) FROM notification WHERE id_notification = ?",
                Long.class, noteId);
        assertAll(
                () -> assertTrue(result.isPresent(), "Read IN_APP sibling should return a non-empty Optional"),
                () -> assertEquals(expectedEpoch, result.get().getEpochSecond(),
                        "Returned Instant epoch should match the stored read_at timestamp")
        );
    }

    @Test
    void checkSiblingNotificationReadStatus_InAppUnread_ReturnsEmpty() {
        // given: an IN_APP notification that exists but has NOT been read (read_at IS NULL)
        // Spring Data JPA 3.x maps a null scalar result to Optional.empty() for Optional<T> return types.
        Long userId = insertUser(false, false);
        insertNotification(userId, TEST_SIBLING_KEY, "IN_APP", "NULL");

        Optional<Instant> result = notificationRepository.checkSiblingNotificationReadStatus(userId, TEST_SIBLING_KEY);

        assertTrue(result.isEmpty(),
                "Unread IN_APP sibling (read_at IS NULL) should return Optional.empty()");
    }

    @Test
    void checkSiblingNotificationReadStatus_NoMatchingSiblingKey_ReturnsEmpty() {
        // given: a notification exists but with a different sibling_key
        Long userId = insertUser(false, false);
        insertNotification(userId, "b".repeat(64), "IN_APP", "NOW()");

        Optional<Instant> result = notificationRepository.checkSiblingNotificationReadStatus(userId, TEST_SIBLING_KEY);

        assertTrue(result.isEmpty(),
                "No notification with TEST_SIBLING_KEY should return Optional.empty()");
    }

    @Test
    void checkSiblingNotificationReadStatus_DifferentUser_ReturnsEmpty() {
        // given: a read IN_APP notification with TEST_SIBLING_KEY owned by a different user
        Long userId1 = insertUser(false, false);
        Long userId2 = insertUser(false, false);
        insertNotification(userId1, TEST_SIBLING_KEY, "IN_APP", "NOW()");

        Optional<Instant> result = notificationRepository.checkSiblingNotificationReadStatus(userId2, TEST_SIBLING_KEY);

        assertTrue(result.isEmpty(),
                "Notification belonging to a different user should return Optional.empty()");
    }

    @Test
    void checkSiblingNotificationReadStatus_ExternalChannelOnly_ReturnsEmpty() {
        // given: a read notification with TEST_SIBLING_KEY but delivery_channel = 'DISCORD' (not IN_APP)
        Long userId = insertUser(false, false);
        insertNotification(userId, TEST_SIBLING_KEY, "DISCORD", "NOW()");

        Optional<Instant> result = notificationRepository.checkSiblingNotificationReadStatus(userId, TEST_SIBLING_KEY);

        assertTrue(result.isEmpty(),
                "Non-IN_APP delivery channel should be excluded — query filters to delivery_channel = 'IN_APP'");
    }

    // ─── findAllDropdownNotificationsByUserId() ───────────────────────────────

    @Test
    void findAllDropdown_ReturnsOnlyInAppDropdownPriorityNotes() {
        // given: 1 IN_APP+dropdown, 1 DISCORD+dropdown, 1 IN_APP+non-dropdown
        Long userId = insertUser(false, false);
        insertNotification(userId, "IN_APP", true, "NULL");
        insertNotification(userId, "DISCORD", true, "NULL");
        insertNotification(userId, "IN_APP", false, "NULL");

        Page<Notification> result =
                notificationRepository.findAllDropdownNotificationsByUserId(userId, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements(),
                "Only the IN_APP + dropdown_priority=true notification should be returned");
    }

    @Test
    void findAllDropdown_ExcludesNonInAppChannels() {
        // given: 1 IN_APP+dropdown and 1 DISCORD+dropdown for the same user
        Long userId = insertUser(false, false);
        insertNotification(userId, "IN_APP", true, "NULL");
        insertNotification(userId, "DISCORD", true, "NULL");

        Page<Notification> result =
                notificationRepository.findAllDropdownNotificationsByUserId(userId, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements(),
                "DISCORD notification with dropdown_priority=true must not appear — query filters to IN_APP only");
    }

    @Test
    void findAllDropdown_ExcludesNonDropdownPriorityNotes() {
        // given: 1 IN_APP+dropdown and 1 IN_APP+non-dropdown for the same user
        Long userId = insertUser(false, false);
        insertNotification(userId, "IN_APP", true, "NULL");
        insertNotification(userId, "IN_APP", false, "NULL");

        Page<Notification> result =
                notificationRepository.findAllDropdownNotificationsByUserId(userId, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements(),
                "IN_APP notification with dropdown_priority=false must not appear");
    }

    // ─── findAllInAppNotificationsByUserId() ─────────────────────────────────

    @Test
    void findAllInApp_ReturnsOnlyInAppNotes() {
        // given: 1 IN_APP, 1 DISCORD, 1 PUSH for the same user
        Long userId = insertUser(false, false);
        insertNotification(userId, "IN_APP", true, "NULL");
        insertNotification(userId, "DISCORD", true, "NULL");
        insertNotification(userId, "PUSH", true, "NULL");

        Page<Notification> result =
                notificationRepository.findAllInAppNotificationsByUserId(userId, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements(),
                "Only the IN_APP notification should be returned — DISCORD and PUSH must be excluded");
    }

    @Test
    void findAllInApp_ExcludesOtherUsersNotes() {
        // given: 1 IN_APP for user A and 1 IN_APP for user B
        Long userAId = insertUser(false, false);
        Long userBId = insertUser(false, false);
        insertNotification(userAId, "IN_APP", true, "NULL");
        insertNotification(userBId, "IN_APP", true, "NULL");

        Page<Notification> result =
                notificationRepository.findAllInAppNotificationsByUserId(userAId, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements(),
                "Query for user A should only return user A's notifications");
    }

    @Test
    void findAllInApp_EmptyResult_WhenNoInAppNotes() {
        // given: only DISCORD and PUSH notifications, no IN_APP
        Long userId = insertUser(false, false);
        insertNotification(userId, "DISCORD", true, "NULL");
        insertNotification(userId, "PUSH", true, "NULL");

        Page<Notification> result =
                notificationRepository.findAllInAppNotificationsByUserId(userId, PageRequest.of(0, 10));

        assertEquals(0, result.getTotalElements(),
                "No IN_APP notifications should yield an empty page");
    }

    // ─── countUnreadByUserId() ────────────────────────────────────────────────

    @Test
    void countUnread_CountsOnlyUnreadInAppNotes() {
        // given: 2 unread IN_APP, 1 read IN_APP, 1 unread DISCORD
        Long userId = insertUser(false, false);
        insertNotification(userId, "IN_APP", true, "NULL");
        insertNotification(userId, "IN_APP", true, "NULL");
        insertNotification(userId, "IN_APP", true, "NOW()");
        insertNotification(userId, "DISCORD", true, "NULL");

        Integer count = notificationRepository.countUnreadByUserId(userId);

        assertEquals(2, count,
                "Only unread IN_APP notifications should be counted");
    }

    @Test
    void countUnread_ExcludesReadInAppNotes() {
        // given: 3 read IN_APP notifications (read_at = NOW())
        Long userId = insertUser(false, false);
        insertNotification(userId, "IN_APP", true, "NOW()");
        insertNotification(userId, "IN_APP", true, "NOW()");
        insertNotification(userId, "IN_APP", true, "NOW()");

        Integer count = notificationRepository.countUnreadByUserId(userId);

        assertEquals(0, count,
                "Read IN_APP notifications must not be counted");
    }

    @Test
    void countUnread_ExcludesNonInAppChannels() {
        // given: unread DISCORD and PUSH notifications only, no IN_APP
        Long userId = insertUser(false, false);
        insertNotification(userId, "DISCORD", true, "NULL");
        insertNotification(userId, "DISCORD", true, "NULL");
        insertNotification(userId, "PUSH", true, "NULL");

        Integer count = notificationRepository.countUnreadByUserId(userId);

        assertEquals(0, count,
                "Unread DISCORD and PUSH notifications must not be counted — query filters to IN_APP only");
    }
}
