package com.sc_fleetfinder.fleets.scheduledTasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxCleanupService {

    private final JdbcTemplate jdbcTemplate;

    int deleteExpiredArchives() {
        return jdbcTemplate.update("""
                DELETE FROM notification_outbox_archive oa
                WHERE oa.archived_at < ( NOW() - INTERVAL 3 MONTH )
                """);
    }

    int archiveOldNotificationOutboxEntries() {
        return jdbcTemplate.update("""
               INSERT INTO notification_outbox_archive (
                   outbox_id, event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
                   payload_json, status, attempt_count, last_error, created_at, locked_at, sent_at,
                   delivery_channel, parent_entity_id, parent_entity_type, sibling_key, error_count,
                   push_sub_id
               )
               SELECT outbox_id, event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
                   payload_json, status, attempt_count, last_error, created_at, locked_at, sent_at,
                   delivery_channel, parent_entity_id, parent_entity_type, sibling_key, error_count,
                   push_sub_id
               FROM notification_outbox ob
               WHERE ob.created_at < ( NOW() - INTERVAL 1 DAY )
               """);
    }

    int deleteRecentlyArchivedNotificationOutboxEntries() {
        return jdbcTemplate.update("""
                DELETE FROM notification_outbox ob
                WHERE ob.created_at < ( NOW() - INTERVAL 1 DAY )
                """);
    }
}
