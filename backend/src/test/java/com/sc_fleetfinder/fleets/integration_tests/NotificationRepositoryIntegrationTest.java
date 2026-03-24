package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
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
}
