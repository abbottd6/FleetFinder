package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface GroupListingRepository extends JpaRepository<GroupListing, Long>, JpaSpecificationExecutor<GroupListing> {

    @Query("select gl.groupId from GroupListing gl where gl.groupId in :ids")
    Set<Long> findAllIds(@Param("ids") Set<Long> ids);

    @Modifying
    @Query(value = """
            INSERT INTO notification_outbox (
                event_type, entity_type, entity_id, entity_owner_id,
                entity_new_status, payload_json, status, created_at
                )
            SELECT
                'LISTING_ARCHIVED'                      AS event_type,
                'GROUP_LISTING'                         AS entity_type,
                gl.id_group                             AS entity_id,
                gl.id_user                              AS entity_owner_id,
                'ARCHIVED'                              AS entity_new_status,
                JSON_OBJECT(
                    'groupId', gl.id_group,
                    'oldStatus', gl.vis_status,
                    'newStatus', 'ARCHIVED'
                )                                       AS payload_json,
                'PENDING'                               AS status,
                NOW()                                   AS created_at
            FROM group_listing gl
            WHERE gl.vis_status = 'ARCHIVED'
              AND gl.last_updated < (NOW() - INTERVAL 14 DAY)
              AND (gl.event_schedule IS NULL OR gl.event_schedule < (NOW() - INTERVAL 14 DAY))
            ON DUPLICATE KEY UPDATE outbox_id = outbox_id
            """, nativeQuery = true)
    int createOutboxEntriesForArchiveNotifications();

    @Modifying
    @Query(value = """
            UPDATE group_listing gl
            SET gl.vis_status = 'ARCHIVED'
                WHERE gl.vis_status <> 'ARCHIVED'
                AND gl.last_updated < (NOW() - INTERVAL 14 DAY)
                AND (gl.event_schedule IS NULL OR gl.event_schedule < (NOW() - INTERVAL 14 DAY))
            """, nativeQuery = true)
    int setArchivedStatus();

    @Modifying
    @Query(value = """
            DELETE gl FROM group_listing gl
            WHERE gl.vis_status = 'ARCHIVED'
                AND gl.last_updated < (NOW() - INTERVAL 14 DAY)
                AND (gl.event_schedule IS NULL OR gl.event_schedule < (NOW() - INTERVAL 14 DAY))
            """, nativeQuery = true)
    int scheduledDeleteArchivedListings();

    @Modifying
    @Query(value = """
            INSERT INTO notification_outbox (
                event_type, entity_type, entity_id, entity_owner_id,
                entity_new_status, payload_json, status, created_at
                )
            SELECT
                'LISTING_VIS_STATUS_CHANGED'                AS event_type,
                'GROUP_LISTING'                             AS entity_type,
                gl.id_group                                 AS entity_id,
                gl.id_user                                  AS entity_owner_id,
                computed.entity_new_status                  AS entity_new_status,
                JSON_OBJECT(
                    'groupId', gl.id_group,
                    'oldStatus', gl.vis_status,
                    'newStatus', computed.entity_new_status
                )                                           AS payload_json,
                'PENDING'                                   AS status,
                NOW()                                       AS created_at
            FROM group_listing gl
            JOIN (
                SELECT
                    id_group,
                    CASE
                        WHEN last_updated < (NOW() - INTERVAL 3 DAY)
                            AND (event_schedule IS NULL OR event_schedule < (NOW() - INTERVAL 3 DAY))
                            THEN 'EXPIRED'
                        WHEN last_updated < (NOW() - INTERVAL 12 HOUR)
                            AND (event_schedule is NULL OR event_schedule < (NOW() - INTERVAL 12 HOUR))
                            THEN 'INACTIVE'
                        WHEN last_updated < (NOW() - INTERVAL 6 HOUR) THEN 'STALE'
                        WHEN last_updated < (NOW() - INTERVAL 3 HOUR) THEN 'RECENT'
                        ELSE vis_status
                    END AS entity_new_status
                FROM group_listing
            ) computed ON computed.id_group = gl.id_group
            WHERE computed.entity_new_status <> gl.vis_status
            ON DUPLICATE KEY UPDATE outbox_id = outbox_id
            """, nativeQuery = true)
    int createNotificationOutboxEntriesForStatusUpdates();

    @Modifying
    @Query(value = """
            UPDATE group_listing gl
            SET gl.vis_status =
                CASE
                    WHEN gl.last_updated < (NOW() - INTERVAL 3 DAY)
                        AND (gl.event_schedule IS NULL OR gl.event_schedule < (NOW() - INTERVAL 3 DAY))
                        THEN 'EXPIRED'
                    WHEN gl.last_updated < (NOW() - INTERVAL 12 HOUR)
                        AND (gl.event_schedule is NULL OR gl.event_schedule < (NOW() - INTERVAL 12 HOUR))
                        THEN 'INACTIVE'
                    WHEN gl.last_updated < (NOW() - INTERVAL 6 HOUR) THEN 'STALE'
                    WHEN gl.last_updated < (NOW() - INTERVAL 3 HOUR) THEN 'RECENT'
                    ELSE gl.vis_status
                END
            WHERE gl.vis_status <>
                CASE
                    WHEN gl.last_updated < (NOW() - INTERVAL 3 DAY)
                        AND (gl.event_schedule IS NULL OR gl.event_schedule < (NOW() - INTERVAL 3 DAY))
                        THEN 'EXPIRED'
                    WHEN gl.last_updated < (NOW() - INTERVAL 12 HOUR)
                        AND (gl.event_schedule is NULL OR gl.event_schedule < (NOW() - INTERVAL 12 HOUR))
                        THEN 'INACTIVE'
                    WHEN gl.last_updated < (NOW() - INTERVAL 6 HOUR) THEN 'STALE'
                    WHEN gl.last_updated < (NOW() - INTERVAL 3 HOUR) THEN 'RECENT'
                    ELSE gl.vis_status
                END
            """, nativeQuery = true)
    int updateListingVisStatuses();

    @Modifying
    @Query(value = """
            UPDATE group_listing gl
            SET gl.vis_status = 'EXPIRED'
            WHERE gl.id_user = :userId
            """, nativeQuery = true)
    void expireAllUserListingsOnDelete(@Param("userId") Long userId);
}
