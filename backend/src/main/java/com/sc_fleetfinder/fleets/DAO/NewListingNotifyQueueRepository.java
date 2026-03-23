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
            WITH matches AS (
                SELECT
                    customNote.id_custom_note, customNote.user_id, customNote.tag_label, gl.id_group,
                    gl.creation_timestamp, gl.listing_title, gl.group_status_id, u.discord_user_id,
                    u.external_group_notes_enabled, push.group_notes_enabled
                FROM new_listing_notify_queue queue
                JOIN group_listing gl ON queue.id_group = gl.id_group
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
                LEFT JOIN push_subscription push ON u.id_user = push.user_id
                WHERE queue.status = 'CLAIMED'
            )
            INSERT INTO notification_outbox (
                        event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
                        payload_json, status, delivery_channel, created_at
                    )
            
            SELECT
                'NEW_LISTING_MATCH'             AS event_type,
                'USER_CUSTOM_NOTIFICATION'      AS entity_type,
                id_custom_note                  AS entity_id,
                user_id                         AS entity_owner_id,
                'MATCHED'                       AS entity_new_status,
                JSON_OBJECT(
                    'tagLabel', tag_label,
                    'targetGroupId', id_group,
                    'listingCreatedAt', creation_timestamp,
                    'listingTitle', listing_title,
                    'groupStatus', group_status_id
                )                               AS payload_json,
                'PENDING'                       AS status,
                'IN_APP'                        AS delivery_channel,
                NOW()                           AS created_at
            FROM matches
            
            UNION ALL
            
            SELECT
                'NEW_LISTING_MATCH'             AS event_type,
                'USER_CUSTOM_NOTIFICATION'      AS entity_type,
                id_custom_note                  AS entity_id,
                user_id                         AS entity_owner_id,
                'MATCHED'                       AS entity_new_status,
                JSON_OBJECT(
                    'tagLabel', tag_label,
                    'targetGroupId', id_group,
                    'listingCreatedAt', creation_timestamp,
                    'listingTitle', listing_title,
                    'groupStatus', group_status_id
                )                               AS payload_json,
                'PENDING'                       AS status,
                'DISCORD'                        AS delivery_channel,
                NOW()                           AS created_at
            FROM matches
            
            WHERE discord_user_id IS NOT NULL
                AND external_group_notes_enabled = 1
            
            UNION ALL
            
            SELECT
                'NEW_LISTING_MATCH'             AS event_type,
                'USER_CUSTOM_NOTIFICATION'      AS entity_type,
                id_custom_note                  AS entity_id,
                user_id                         AS entity_owner_id,
                'MATCHED'                       AS entity_new_status,
                JSON_OBJECT(
                    'tagLabel', tag_label,
                    'targetGroupId', id_group,
                    'listingCreatedAt', creation_timestamp,
                    'listingTitle', listing_title,
                    'groupStatus', group_status_id
                )                               AS payload_json,
                'PENDING'                       AS status,
                'PUSH'                          AS delivery_channel,
                NOW()                           AS created_at
            FROM matches
            
            WHERE group_notes_enabled = 1
            
            ON DUPLICATE KEY UPDATE outbox_id = outbox_id
            """, nativeQuery = true)
    int generateNotificationOutboxEntriesForNewListingQueueOnCustomNoteMatches(
            // This selects group_listing entities using the new_listing_queue ids.
            // Then joins user_custom_notification table, and joins users table.

            // Then it matches user_custom_notification fields to the group listings and inserts...
                // into the notification_outbox, setting delivery_channel to 'IN_APP'.
            // From those custom_notification <-> group_listing 'matches' it selects users who have...
                // a discord_user_id and their external_group notifications enabled...
                // to insert another set of outbox_notifications with the delivery channel set to 'DISCORD'.
            // Then it takes the initial set 'matches' again, and the join from custom_notes <-> users...
                // to join those users with push_subscriptions and select users with push notifications
                // that have groupNotesEnabled, and insert that set of outbox_notifications with the
                // delivery_channel set to 'PUSH'

            // duplicates are prevented using the notification_outbox unique key, which allows multiple
                // outbox entries for the same 'notification' — IF they have different channels.
                // It does not allow multiple entries with the same channel (in combination with other fields)
                // This is intended so that notifications can be sent to all 'enabled' channels.
            // A user can have multiple push notification devices configured, so when the outbox_notification
                // for a "PUSH" channel is sent, it sends to all push_subscriptions for a user.
    );

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
                AND locked_at IS NULL OR locked_at < (NOW() - INTERVAL 2 MINUTE))
            """, nativeQuery = true)
    int markProcessed(@Param("groupId") long groupId);

    @Modifying
    @Query(value = """
            DELETE FROM new_listing_notify_queue
            WHERE status = 'PROCESSED'
              AND processed_at < (NOW() - INTERVAL 1 DAY)
            """, nativeQuery = true)
    int deleteOldProcessedEntries();
}
