package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.NewListingNotifyQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewListingNotifyQueueRepository extends JpaRepository<NewListingNotifyQueue, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO notification_outbox (
                        event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
                        parent_entity_id, parent_entity_type, payload_json, status,
                        delivery_channel, created_at
                    )
            SELECT
                'NEW_LISTING_MATCH'             AS event_type,
                'GROUP_LISTING'                 AS entity_type,
                gl.id_group                     AS entity_id,
                customNote.user_id              AS entity_owner_id,
                'MATCHED'                       AS entity_new_status,
                customNote.id_custom_note       AS parent_entity_id,
                'USER_CUSTOM_NOTIFICATION'      AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',        customNote.tag_label,
                    'targetId',         gl.id_group,
                    'targetLabel',      gl.listing_title,
                    'targetCreatedAt',  gl.creation_timestamp,
                    'addContext',       group_status.group_status
                )                               AS payload_json,
                'PENDING'                       AS status,
                channels.delivery_channel       AS delivery_channel,
                NOW()                           AS created_at
            FROM new_listing_notify_queue queue
            JOIN group_listing gl ON queue.id_group = gl.id_group
            JOIN group_status ON gl.group_status_id = group_status.group_status_id
            JOIN user_custom_notification customNote ON
                customNote.enabled = 1
                AND (customNote.user_id != gl.id_user)
                AND (customNote.server_id IS NULL OR customNote.server_id = gl.server_id)
                AND (customNote.environment_id IS NULL OR customNote.environment_id = gl.environment_id)
                AND (customNote.experience_id IS NULL OR customNote.experience_id = gl.experience_id)
                AND (customNote.category_id IS NULL OR customNote.category_id = gl.category_id)
                AND (customNote.subcategory_id IS NULL OR customNote.subcategory_id = gl.subcategory_id)
                AND (customNote.system_id IS NULL OR customNote.system_id = gl.system_id)
                AND (customNote.language_code IS NULL OR customNote.language_code = gl.language_code)
                AND (customNote.pvp_status_id IS NULL OR customNote.pvp_status_id = gl.pvp_status_id)
                AND (customNote.legality_id IS NULL OR customNote.legality_id = gl.legality_id)
                AND (customNote.group_status_id IS NULL OR customNote.group_status_id = gl.group_status_id)
            JOIN users u ON customNote.user_id = u.id_user
            CROSS JOIN (
                SELECT 'IN_APP'  AS delivery_channel UNION ALL
                SELECT 'DISCORD' UNION ALL
                SELECT 'PUSH'
            ) AS channels
            LEFT JOIN push_subscription push
                ON push.user_id = u.id_user
                AND channels.delivery_channel = 'PUSH'
                AND push.group_notes_enabled = 1
            WHERE queue.status = 'CLAIMED'
              AND (
                channels.delivery_channel = 'IN_APP'
                OR (channels.delivery_channel = 'DISCORD'
                    AND u.discord_user_id IS NOT NULL
                    AND u.external_group_notes_enabled = 1)
                OR (channels.delivery_channel = 'PUSH'
                    AND push.group_notes_enabled = 1)
              )
            ON DUPLICATE KEY UPDATE outbox_id = outbox_id
            """, nativeQuery = true)
    int generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches();
            // Joins new_listing_notify_queue → group_listing → user_custom_notification → users.
            // CROSS JOINs a 3-row inline table (IN_APP, DISCORD, PUSH) to generate one candidate row
            // per match per channel in a single pass — the main JOIN is not repeated.
            //
            // entity_id   = gl.id_group   (the listing that triggered the match)
            // entity_owner_id = customNote.user_id  (the user receiving the notification)
            // Unique key (event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            //             delivery_channel) → one entry per user per listing per channel.
            //
            // Channel filtering (WHERE):
            //   IN_APP  — all matches
            //   DISCORD — matches where the user has a discord_user_id and external_group_notes enabled
            //   PUSH    — matches where the user has a push_subscription with group_notes_enabled
            //
            // The push_subscription LEFT JOIN is conditioned on channels.delivery_channel = 'PUSH',
            // so it only fires for PUSH rows. IN_APP and DISCORD rows produce exactly 1 row per match
            // regardless of how many push subscriptions a user has (avoiding row multiplication).
            // Users with multiple push subscriptions produce multiple PUSH rows; ON DUPLICATE KEY UPDATE
            // deduplicates them — the single PUSH outbox entry is then sent to all their subscriptions.

    @Modifying
    @Query(value = """
            UPDATE new_listing_notify_queue
            SET status = 'CLAIMED', locked_at = NOW()
            WHERE status = 'IN_QUEUE'
              AND (locked_at IS NULL OR locked_at < (NOW() - INTERVAL 2 MINUTE))
            ORDER BY queued_at
            LIMIT :limit
            """, nativeQuery = true)
    int claimForProcessing(@Param("limit") int limit);

    @Query(value = """
            SELECT * FROM new_listing_notify_queue
            WHERE status = 'CLAIMED'
              AND locked_at >= (NOW() - INTERVAL 2 MINUTE)
            ORDER BY queued_at
            LIMIT :limit
            """, nativeQuery = true)
    List<NewListingNotifyQueue> findClaimedItems(@Param("limit") int limit);

    @Modifying
    @Query(value = """
            UPDATE new_listing_notify_queue
            SET status = 'PROCESSED', processed_at = NOW()
            WHERE status = 'CLAIMED'
                AND (locked_at IS NULL OR locked_at < (NOW() - INTERVAL 2 MINUTE))
            """, nativeQuery = true)
    int markProcessed();

    @Modifying
    @Query(value = """
            DELETE FROM new_listing_notify_queue
            WHERE status = 'PROCESSED'
              AND processed_at < (NOW() - INTERVAL 3 HOUR)
            """, nativeQuery = true)
    int deleteOldProcessedEntries();
}
