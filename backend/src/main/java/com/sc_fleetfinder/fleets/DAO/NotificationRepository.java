package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
            SELECT * FROM notification n
            WHERE n.id_user = :userId
                AND n.dropdown_priority = true
                AND n.delivery_channel = 'IN_APP'
            ORDER BY n.created_at DESC
            """, nativeQuery = true)
    Page<Notification> findAllDropdownNotificationsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT * FROM notification n
            WHERE n.id_user = :userId
                AND n.delivery_channel = 'IN_APP'
            ORDER BY n.created_at DESC
            """, nativeQuery = true)
    Page<Notification> findAllInAppNotificationsByUserId(@Param("userId") Long userId, Pageable pageable);

    Integer deleteAllByUser_userId(Long userId);

    @Query(value = """
            SELECT COUNT(*)
            FROM notification n
            WHERE n.id_user = :userId
                AND n.read_at IS NULL
                AND n.delivery_channel = 'IN_APP'
            """, nativeQuery = true)
    Integer countUnreadByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Notification n
            SET n.readAt = CURRENT_TIMESTAMP
            WHERE n.user.userId = :userId
                AND n.notificationId IN :readIds
                AND n.readAt IS NULL
            """)
    int markAsRead(@Param("userId") Long userId,
                                  @Param("readIds")Collection<Long> readIds);

    @Modifying
    @Query(value = """
            INSERT IGNORE INTO notification_outbox (
            event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            parent_entity_id, parent_entity_type, payload_json, status, push_sub_id,
            delivery_channel, sibling_key, created_at
            )
            SELECT
                'MOD_DELETE'                AS event_type,
                'LISTING_ARCHIVE'           AS entity_type,
                action.id_archive           AS entity_id,
                action.id_user              AS entity_owner_id,
                'Actioned'                  AS entity_new_status,
                action.id_action            AS parent_entity_id,
                'MOD_LISTING_ACTION'        AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',        SUBSTRING(archive.listing_title, 1, 100),
                    'targetId',         action.id_action,
                    'targetLabel',      action.action_type,
                    'targetCreatedAt',  action.action_ts,
                    'addContext',       action.action_note
                )                           AS payload_json,
                'PENDING'                   AS status,
                push.id_push_sub            AS push_sub_id,
                channels.delivery_channel   AS delivery_channel,
                SHA2(CONCAT('MOD_DELETE', '|', 'LISTING_ARCHIVE', '|', action.id_archive, '|', action.id_user, '|', 'ARCHIVED'), 256) AS sibling_key,
                NOW()                       as created_at
                FROM mod_listing_action action
                JOIN listing_archive archive ON action.id_archive = archive.id_archive
                JOIN users u ON action.id_user = u.id_user
                CROSS JOIN (
                    SELECT 'IN_APP' AS delivery_channel UNION ALL
                    SELECT 'DISCORD' UNION ALL
                    SELECT 'PUSH'
                ) AS channels
                LEFT JOIN push_subscription push
                    ON push.user_id = action.id_user
                    AND channels.delivery_channel = 'PUSH'
                    AND push.sys_notes_enabled = 1
                WHERE action.id_action = :actionId
                    AND (
                        channels.delivery_channel = 'IN_APP'
                        OR (channels.delivery_channel = 'DISCORD'
                            AND u.discord_user_id IS NOT NULL
                            AND u.external_sys_notes_enabled = 1)
                        OR (channels.delivery_channel = 'PUSH'
                            AND push.sys_notes_enabled = 1)
                    )
            """, nativeQuery = true)
    int generateOutboxNotificationsOnModListingDelete(@Param("actionId") Long modActionId);

    @Query(value = """
            SELECT read_at
            FROM notification n
            WHERE n.id_user = :userId
                AND n.sibling_key = :siblingKey
                AND n.delivery_channel = 'IN_APP'
            """, nativeQuery = true)
    Optional<LocalDateTime> checkSiblingNotificationReadStatus(@Param("userId") Long userId, @Param("siblingKey") String siblingKey);
}
