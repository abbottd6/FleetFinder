package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberId;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    Page<GroupMember> findAllByUserOrderByCreatedAtDesc(Users user, Pageable pageable);

    @Query(value = """
            SELECT * FROM group_member m
            JOIN group_listing l ON m.listing_id = l.id_group
            WHERE m.user_id = :userId
            ORDER BY
                CASE
                    WHEN l.event_schedule IS NOT NULL
                    THEN ABS(TIMESTAMPDIFF(SECOND, NOW(), l.event_schedule))
                    ELSE ABS(TIMESTAMPDIFF(SECOND, NOW(), l.creation_timestamp))
                END
            """,
            countQuery = "SELECT count(*) FROM group_member WHERE user_id = :userId",
            nativeQuery = true)
    Page<GroupMember> findAllByUserOrderByEventTimeProximity(@Param("userId") Long userId, Pageable pageable);

    Optional<GroupMember> findByUserUserIdAndGroupListing(Long userId, GroupListing listing);

    @Query("""
            SELECT m FROM GroupMember m
            WHERE m.groupListing.groupId = :listingId
                AND m.memberStatus = 'ACTIVE'
            """)
    Page<GroupMember> findActiveRosterMembersByGroup(@Param("listingId") Long listingId, Pageable pageable);

    @Query("""
            SELECT m FROM GroupMember m
            WHERE m.groupListing.groupId = :listingId
                AND m.memberStatus = 'WAITLIST'
            """)
    Page<GroupMember> findWaitlistMembersByGroup(@Param("listingId") Long listingId, Pageable pageable);

    @Modifying
    @Query(value = """
           INSERT IGNORE INTO notification_outbox (
            event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            parent_entity_id, parent_entity_type, payload_json, status, push_sub_id,
            delivery_channel, do_not_duplicate, sibling_key, created_at
            )
           SELECT
                :noteType                   AS event_type,
                'group_listing'             AS entity_type,
                :listingId                  AS entity_id,
                :recipientId                AS entity_owner_id,
                :formerMemberStatus         AS entity_new_status,
                :formerMemberUserId         AS parent_entity_id,
                'users'                     AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',            'A member has left your group.',
                    'targetId',             :formerMemberUserId,
                    'targetLabel',          :formerMemberUsername,
                    'targetStatus',         :formerMemberStatus,
                    'contextElementLabel',  :listingTitle
                )                           AS payload_json,
                'PENDING'                   AS status,
                push.id_push_sub            AS push_sub_id,
                channels.delivery_channel   AS delivery_channel,
                channels.do_not_duplicate   AS do_not_duplicate,
                SHA2(CONCAT(:noteType, '|', 'group_member', '|', :formerMemberUserId, '|', :recipientId, '|', :leftAtTimestamp), 256) AS sibling_key,
                NOW()                       AS created_at
           FROM users user
           CROSS JOIN (
                SELECT 'IN_APP' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'DISCORD' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'PUSH' AS delviery_channel, NULL AS do_not_duplicate
           ) AS channels
           LEFT JOIN push_subscription push
                ON push.user_id = :recipientId
                AND channels.delivery_channel = 'PUSH'
                AND push.group_notes_enabled = 1
           WHERE user.id_user = :recipientId
                AND (
                    channels.delivery_channel = 'IN_APP'
                    OR (channels.delivery_channel = 'DISCORD'
                        AND user.discord_user_id IS NOT NULL
                        AND user.external_group_notes_enabled = 1)
                    OR (channels.delivery_channel = 'PUSH'
                        AND push.group_notes_enabled = 1)
                )
           """, nativeQuery = true)
    Integer generateOutboxNotesForGroupMemberLeft(@Param("noteType") String noteType,
                                                  @Param("formerMemberUserId") Long userId,
                                                  @Param("formerMemberUsername") String username,
                                                  @Param("formerMemberStatus") String status,
                                                  @Param("recipientId") Long recipientId,
                                                  @Param("listingId") Long listingId,
                                                  @Param("listingTitle") String listingTitle,
                                                  @Param("leftAtTimestamp") String leftAtTimestamp);

    @Modifying
    @Query(value = """
           INSERT IGNORE INTO notification_outbox (
            event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            parent_entity_id, parent_entity_type, payload_json, status, push_sub_id,
            delivery_channel, do_not_duplicate, sibling_key, created_at
            )
           SELECT
                :noteType                   AS event_type,
                'group_listing'             AS entity_type,
                :listingId                  AS entity_id,
                :formerMemberUserId         AS entity_owner_id,
                'Removed'                   AS entity_new_status,
                :formerMemberUserId         AS parent_entity_id,
                'users'                     AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',            'You were removed from a group.',
                    'targetId',             :formerMemberUserId,
                    'targetLabel',          :listingTitle,
                    'targetStatus',         NULL,
                    'contextElementLabel',  NULL
                )                           AS payload_json,
                'PENDING'                   AS status,
                push.id_push_sub            AS push_sub_id,
                channels.delivery_channel   AS delivery_channel,
                channels.do_not_duplicate   AS do_not_duplicate,
                SHA2(CONCAT(:noteType, '|', 'group_listing', '|', :formerMemberUserId, '|', :listingOwnerId, '|', :removalTimestamp), 256) AS sibling_key,
                NOW()                       AS created_at
           FROM users user
           CROSS JOIN (
                SELECT 'IN_APP' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'DISCORD' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'PUSH' AS delviery_channel, NULL AS do_not_duplicate
           ) AS channels
           LEFT JOIN push_subscription push
                ON push.user_id = :formerMemberUserId
                AND channels.delivery_channel = 'PUSH'
                AND push.group_notes_enabled = 1
           WHERE user.id_user = :formerMemberUserId
                AND (
                    channels.delivery_channel = 'IN_APP'
                    OR (channels.delivery_channel = 'DISCORD'
                        AND user.discord_user_id IS NOT NULL
                        AND user.external_group_notes_enabled = 1)
                    OR (channels.delivery_channel = 'PUSH'
                        AND push.group_notes_enabled = 1)
                )
           """, nativeQuery = true)
    Integer generateOutboxNotesForRemovedFromGroupNotifyEvent(@Param("noteType") String noteType,
                                                              @Param("formerMemberUserId") Long userId,
                                                              @Param("listingTitle") String listingTitle,
                                                              @Param("listingId") Long listingId,
                                                              @Param("listingOwnerId") Long listingOwnerId,
                                                              @Param("removalTimestamp") String removalTs);

}
