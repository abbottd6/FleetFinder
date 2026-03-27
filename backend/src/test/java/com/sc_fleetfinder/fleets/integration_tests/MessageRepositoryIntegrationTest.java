package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
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
public class MessageRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Long insertUser(boolean withDiscord, boolean socialNotesEnabled) {
        String uuid = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO users (keycloak_id, user_name, email, external_sys_notes_enabled, " +
                "external_group_notes_enabled, external_social_notes_enabled, discord_user_id) " +
                "VALUES (?, ?, ?, 0, 0, ?, ?)",
                uuid,
                "user_" + uuid.substring(0, 8),
                uuid.substring(0, 8) + "@test.com",
                socialNotesEnabled ? 1 : 0,
                withDiscord ? "123456789012345678" : null
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_user) FROM users", Long.class);
    }

    private Long insertConversation(Long initiatingUserId) {
        jdbcTemplate.update(
                "INSERT INTO conversation (type, title, initiated_by_user_id) VALUES ('DIRECT', 'Test Conversation', ?)",
                initiatingUserId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_conversation) FROM conversation", Long.class);
    }

    private Long insertMessage(Long convId, Long senderId) {
        jdbcTemplate.update(
                "INSERT INTO message (id_conversation, id_sender, message_type, msg_body) " +
                "VALUES (?, ?, 'TEXT', 'Hello!')",
                convId, senderId
        );
        return jdbcTemplate.queryForObject("SELECT MAX(id_msg) FROM message", Long.class);
    }

    // Inserts participant with last_read_message_id = NULL (unread)
    private void insertParticipant(Long convId, Long userId) {
        jdbcTemplate.update(
                "INSERT INTO conversation_participant (id_conversation, id_user, role) VALUES (?, ?, 'MEMBER')",
                convId, userId
        );
    }

    // Inserts participant where last_read_message_id points to an existing message (already read)
    private void insertParticipantWithReadPointer(Long convId, Long userId, Long lastReadMsgId) {
        jdbcTemplate.update(
                "INSERT INTO conversation_participant (id_conversation, id_user, role, last_read_message_id) " +
                "VALUES (?, ?, 'MEMBER', ?)",
                convId, userId, lastReadMsgId
        );
    }

    // Inserts an in-app notification linking the recipient to the conversation.
    // createdAtExpr is a SQL expression: "DATE_SUB(NOW(), INTERVAL 10 MINUTE)" for old,
    // "NOW()" for fresh (< 5 min — will not satisfy the query's age check).
    private void insertNewMessageNotification(Long recipientId, Long convId, String createdAtExpr) {
        jdbcTemplate.update(
                "INSERT INTO notification (id_user, type, message, parent_entity_id, parent_entity_type, created_at) " +
                "VALUES (?, 'NEW_MESSAGE', 'You have a new message', ?, 'CONVERSATION', " + createdAtExpr + ")",
                recipientId, convId
        );
    }

    private void insertPushSubscription(Long userId, boolean socialNotesEnabled) {
        jdbcTemplate.update(
                "INSERT INTO push_subscription (user_id, user_label, device_url, public_key, browser_secret, social_notes_enabled) " +
                "VALUES (?, 'Test Device', 'https://push.example.com/token', 'pk123', 'sec123', ?)",
                userId, socialNotesEnabled ? 1 : 0
        );
    }

    // ─── generateExternalDeliveryOutboxNotifications() ───────────────────────

    @Test
    void generateExternal_UserHasNoExternalChannels_NewMessageProducesNoEntry() {
        // given: no external delivery_channel exists for this conversation (inner JOIN fails)
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(false, false);
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE'",
                Integer.class, msgId);
        assertEquals(0, count, "No in-app notification for conversation should produce no outbox entries");
    }

    @Test
    void generateExternal_OldNotification_Discord_CreatesDiscordEntry() {
        // given: discord-enabled recipient, in-app notification older than 5 minutes
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(true, true);  // discord + social_notes_enabled
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        int inserted = messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'DISCORD'",
                Integer.class, msgId);
        Integer inAppCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'IN_APP'",
                Integer.class, msgId);
        assertAll(
                () -> assertTrue(inserted >= 1, "Expected at least 1 row inserted"),
                () -> assertEquals(1, discordCount, "Discord-enabled user should get exactly 1 DISCORD entry"),
                () -> assertEquals(0, inAppCount, "External delivery query must not create IN_APP entries")
        );
    }

    @Test
    void generateExternal_OldNotification_Discord_Idempotent_SecondCallIsNoop() {
        // given: same setup as above — second call should not create duplicates
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(true, true);
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);
        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'DISCORD'",
                Integer.class, msgId);
        assertEquals(1, discordCount, "Second call must not create a duplicate entry (ON DUPLICATE KEY)");
    }

    @Test
    void generateExternal_OldNotification_Push_CreatesPushEntry() {
        // given: push-enabled recipient, in-app notification older than 5 minutes
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(false, false);
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertPushSubscription(recipientId, true);  // social_notes_enabled = 1
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'PUSH'",
                Integer.class, msgId);
        Integer inAppCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'IN_APP'",
                Integer.class, msgId);
        assertAll(
                () -> assertEquals(1, pushCount, "Push-enabled user should get exactly 1 PUSH entry"),
                () -> assertEquals(0, inAppCount, "External delivery query must not create IN_APP entries")
        );
    }

    @Test
    void generateExternal_DiscordAndPushBothEnabled_CreatesBothEntries() {
        // given: user has discord and push with social notes enabled
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(true, true);  // discord + social_notes_enabled
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertPushSubscription(recipientId, true);
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer totalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE'",
                Integer.class, msgId);
        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'DISCORD'",
                Integer.class, msgId);
        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'PUSH'",
                Integer.class, msgId);
        assertAll(
                () -> assertEquals(2, totalCount, "Discord + Push both enabled should yield exactly 2 entries"),
                () -> assertEquals(1, discordCount),
                () -> assertEquals(1, pushCount)
        );
    }

    @Test
    void generateExternal_RecentNotification_NoEntry() {
        // given: in-app notification created just now (< 5 minutes) — query excludes it
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(true, true);
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertNewMessageNotification(recipientId, convId, "NOW()");  // too recent

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE'",
                Integer.class, msgId);
        assertEquals(0, count, "Notification created less than 5 minutes ago should not trigger external delivery");
    }

    @Test
    void generateExternal_RecipientAlreadyRead_NoEntry() {
        // given: recipient's last_read_message_id points to a newer message than :msgId
        // meaning they have already implicitly read the target message
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(true, true);
        Long convId = insertConversation(senderId);
        Long olderMsgId = insertMessage(convId, senderId);  // the "old" message to notify about
        Long newerMsgId = insertMessage(convId, senderId);  // a newer message the recipient has read
        // recipient has read up to newerMsgId, which is > olderMsgId
        insertParticipantWithReadPointer(convId, recipientId, newerMsgId);
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, olderMsgId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE'",
                Integer.class, olderMsgId);
        assertEquals(0, count, "Recipient who has read a newer message should not receive external notification");
    }

    @Test
    void generateExternal_DiscordDisabled_NoDiscordEntry() {
        // given: user has discord_user_id but external_social_notes_enabled = 0
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(true, false);  // discord ID, but social notes disabled
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer discordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'DISCORD'",
                Integer.class, msgId);
        assertEquals(0, discordCount, "No DISCORD entry when external_social_notes_enabled = 0");
    }

    @Test
    void generateExternal_PushSocialNotesDisabled_NoPushEntry() {
        // given: push subscription with social_notes_enabled = 0
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(false, false);
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertPushSubscription(recipientId, false);  // social_notes_enabled = 0
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'PUSH'",
                Integer.class, msgId);
        assertEquals(0, pushCount, "No PUSH entry when push.social_notes_enabled = 0");
    }

    @Test
    void generateExternal_MultiplePushSubs_OnlyOnePushEntry() {
        // given: 2 push subscriptions both with social_notes_enabled = 1
        // ON DUPLICATE KEY UPDATE should deduplicate to exactly 1 PUSH outbox entry
        Long senderId = insertUser(false, false);
        Long recipientId = insertUser(false, false);
        Long convId = insertConversation(senderId);
        Long msgId = insertMessage(convId, senderId);
        insertParticipant(convId, recipientId);
        insertPushSubscription(recipientId, true);
        insertPushSubscription(recipientId, true);
        insertNewMessageNotification(recipientId, convId, "DATE_SUB(NOW(), INTERVAL 10 MINUTE)");

        messageRepository.generateExternalDeliveryOutboxNotifications(recipientId, msgId);

        Integer pushCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE entity_id = ? AND event_type = 'NEW_CHAT_MESSAGE' AND delivery_channel = 'PUSH'",
                Integer.class, msgId);
        assertEquals(1, pushCount, "2 push subscriptions should still produce exactly 1 PUSH entry (ON DUPLICATE KEY dedup)");
    }
}
