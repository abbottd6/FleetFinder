package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class NotificationOutboxRepositoryIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private NotificationOutboxRepository outboxRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // Helper: inserts a PENDING outbox entry and returns its outbox_id
    private Long insertPendingOutboxEntry() {
        jdbcTemplate.update(
                "INSERT INTO notification_outbox " +
                "(event_type, entity_type, entity_id, entity_owner_id, entity_new_status, status, created_at) " +
                "VALUES ('LISTING_VIS_STATUS_CHANGED', 'GROUP_LISTING', 1, 1, 'EXPIRED', 'PENDING', NOW())"
        );
        return jdbcTemplate.queryForObject("SELECT MAX(outbox_id) FROM notification_outbox", Long.class);
    }

    // Helper: inserts a PENDING outbox entry with a unique entity_new_status to avoid UNIQUE KEY conflicts
    private Long insertPendingOutboxEntry(String entityNewStatus) {
        jdbcTemplate.update(
                "INSERT INTO notification_outbox " +
                "(event_type, entity_type, entity_id, entity_owner_id, entity_new_status, status, created_at) " +
                "VALUES ('LISTING_VIS_STATUS_CHANGED', 'GROUP_LISTING', 1, 1, ?, 'PENDING', NOW())",
                entityNewStatus
        );
        return jdbcTemplate.queryForObject("SELECT MAX(outbox_id) FROM notification_outbox", Long.class);
    }

    // ─── claimStatus_Pending() ────────────────────────────────────────────────

    @Test
    void claimStatus_Pending_PendingEntry_BecomesProcessing() {
        // given: one PENDING entry with locked_at IS NULL
        Long id = insertPendingOutboxEntry();

        // when
        int count = outboxRepo.claimStatus_Pending(100);

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        Integer attemptCount = jdbcTemplate.queryForObject(
                "SELECT attempt_count FROM notification_outbox WHERE outbox_id = ?", Integer.class, id);
        Integer lockedAtNull = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE outbox_id = ? AND locked_at IS NULL",
                Integer.class, id);
        assertAll(
                () -> assertTrue(count >= 1, "Expected at least 1 entry claimed"),
                () -> assertEquals("PROCESSING", status, "Status should be PROCESSING after claim"),
                () -> assertEquals(1, attemptCount, "attempt_count should be incremented to 1"),
                () -> assertEquals(0, lockedAtNull, "locked_at should not be null after claim")
        );
    }

    @Test
    void claimStatus_Pending_RespectsLimit() {
        // given: 3 PENDING entries with distinct entity_new_status values to avoid UNIQUE KEY conflicts
        insertPendingOutboxEntry("EXPIRED");
        insertPendingOutboxEntry("INACTIVE");
        insertPendingOutboxEntry("STALE");

        // when: claim with limit 2
        int count = outboxRepo.claimStatus_Pending(2);

        // then
        Integer processingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE status = 'PROCESSING'", Integer.class);
        assertAll(
                () -> assertEquals(2, count, "Should claim exactly 2 entries"),
                () -> assertEquals(2, processingCount, "Exactly 2 entries should be PROCESSING")
        );
    }

    @Test
    void claimStatus_Pending_StaleProcessingEntry_IsReclaimed() {
        // given: entry in PROCESSING state with a stale locked_at (3 minutes ago)
        Long id = insertPendingOutboxEntry();
        jdbcTemplate.update(
                "UPDATE notification_outbox SET status = 'PENDING', " +
                "locked_at = DATE_SUB(NOW(), INTERVAL 3 MINUTE), attempt_count = 1 " +
                "WHERE outbox_id = ?",
                id
        );

        // when
        int count = outboxRepo.claimStatus_Pending(100);

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        Integer attemptCount = jdbcTemplate.queryForObject(
                "SELECT attempt_count FROM notification_outbox WHERE outbox_id = ?", Integer.class, id);
        assertAll(
                () -> assertTrue(count >= 1, "Stale PROCESSING entry should be reclaimed"),
                () -> assertEquals("PROCESSING", status),
                () -> assertEquals(2, attemptCount, "attempt_count should be incremented again")
        );
    }

    @Test
    void claimStatus_Pending_RecentlyLockedEntry_IsNotReclaimed() {
        // given: entry in PENDING state but with a fresh locked_at (just now — not stale)
        Long id = insertPendingOutboxEntry();
        jdbcTemplate.update(
                "UPDATE notification_outbox SET locked_at = NOW() WHERE outbox_id = ?", id
        );

        // when
        int count = outboxRepo.claimStatus_Pending(100);

        // then: the WHERE clause requires (locked_at IS NULL OR locked_at < NOW() - 2 MINUTES)
        // locked_at = NOW() does not satisfy locked_at < NOW() - 2 MINUTES
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        assertEquals("PENDING", status, "Entry with fresh locked_at should not be reclaimed");
    }

    // ─── findStatus_Claimed() ─────────────────────────────────────────────────

    @Test
    void findStatus_Claimed_StaleProcessingEntry_IsReturned() {
        // given: PROCESSING entry with locked_at 3 minutes ago (stale — exceeds 2-minute threshold)
        Long id = insertPendingOutboxEntry();
        jdbcTemplate.update(
                "UPDATE notification_outbox SET status = 'PROCESSING', " +
                "locked_at = DATE_SUB(NOW(), INTERVAL 3 MINUTE) " +
                "WHERE outbox_id = ?",
                id
        );

        // when
        List<NotificationOutbox> result = outboxRepo.findStatus_Claimed(100);

        // then
        boolean found = result.stream().anyMatch(e -> e.getOutboxId().equals(id));
        assertTrue(found, "Stale PROCESSING entry should be returned by findStatus_Claimed");
    }

    @Test
    void findStatus_Claimed_FreshlyClaimedEntry_IsNotReturned() {
        // given: PROCESSING entry with locked_at = NOW() (freshly claimed, not stale)
        Long id = insertPendingOutboxEntry();
        jdbcTemplate.update(
                "UPDATE notification_outbox SET status = 'PROCESSING', locked_at = NOW() " +
                "WHERE outbox_id = ?",
                id
        );

        // when
        List<NotificationOutbox> result = outboxRepo.findStatus_Claimed(100);

        // then
        boolean found = result.stream().anyMatch(e -> e.getOutboxId().equals(id));
        assertFalse(found, "Freshly claimed PROCESSING entry should not be returned by findStatus_Claimed");
    }

    // ─── markSent() ──────────────────────────────────────────────────────────

    @Test
    void markSent_SetsStatusToSentAndRecordsSentAt() {
        // given
        Long id = insertPendingOutboxEntry();

        // when
        int count = outboxRepo.markSent(id);

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        Integer sentAtNull = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE outbox_id = ? AND sent_at IS NULL",
                Integer.class, id);
        assertAll(
                () -> assertEquals(1, count),
                () -> assertEquals("SENT", status),
                () -> assertEquals(0, sentAtNull, "sent_at should be set after markSent")
        );
    }

    // ─── markFailed() ────────────────────────────────────────────────────────

    @Test
    void markFailed_SetsStatusToFailedAndStoresError() {
        // given
        Long id = insertPendingOutboxEntry();

        // when
        int count = outboxRepo.markFailed(id, "connection timeout");

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        String lastError = jdbcTemplate.queryForObject(
                "SELECT last_error FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        assertAll(
                () -> assertEquals(1, count),
                () -> assertEquals("FAILED", status),
                () -> assertEquals("connection timeout", lastError)
        );
    }

    @Test
    void markFailed_NullError_StoresNull() {
        // given
        Long id = insertPendingOutboxEntry();

        // when
        outboxRepo.markFailed(id, null);

        // then
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM notification_outbox WHERE outbox_id = ?", String.class, id);
        Integer lastErrorNull = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notification_outbox WHERE outbox_id = ? AND last_error IS NULL",
                Integer.class, id);
        assertAll(
                () -> assertEquals("FAILED", status),
                () -> assertEquals(1, lastErrorNull, "last_error should be NULL")
        );
    }
}
