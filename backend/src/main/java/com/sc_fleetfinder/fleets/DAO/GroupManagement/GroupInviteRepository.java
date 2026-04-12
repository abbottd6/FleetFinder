package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {

    @Query("""
            SELECT i FROM GroupInvite i
            WHERE i.sender = :sender
                AND i.recipient = :recipient
                AND i.groupListing.groupId = :listingId
                AND i.inviteDirection = :dir
            """)
    Optional<GroupInvite> findBySenderListingAndDirection(
            @Param("sender") Users sender,
            @Param("recipient") Users recip,
            @Param("listingId") Long listingId,
            @Param("dir") InviteDirection dir);

    @Modifying
    @Query("""
            DELETE FROM GroupInvite gi
            WHERE gi.groupListing.groupId = :listingId
                        AND ((gi.sender.userId = :userId AND gi.inviteDirection = "REQUEST")
                        OR (gi.recipient.userId = :userId AND gi.inviteDirection = "OFFER"))
            """)
    void deleteByUserAndGroupListing(@Param("userId") Long userId, @Param("listingId") Long listingId);

    @Query("""
            SELECT inv FROM GroupInvite inv
            WHERE inv.groupListing.groupId = :groupId
            """)
    Page<GroupInvite> findPageOfAllGroupInvitesByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    @Modifying
    @Query(value = """
            INSERT IGNORE INTO notification_outbox (
            event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            parent_entity_id, parent_entity_type, payload_json, status, push_sub_id,
            delivery_channel, do_not_duplicate, sibling_key, created_at
            )
            SELECT
                'NEW_GROUP_INVITE'              AS event_type,
                'GROUP_INVITE'                  AS entity_type,
                inv.id_invite                   AS entity_id,
                inv.recipient_id                AS entity_owner_id,
                inv.direction                   AS entity_new_status,
                inv.listing_id                  AS parent_entity_id,
                'GROUP_LISTING'                 AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',        sender.user_name,
                    'targetId',         inv.sender_id,
                    'targetLabel',      grp.listing_title,
                    'targetCreatedAt',  inv.created_at,
                    'addContext',       SUBSTRING(inv.invite_message, 1, 64)
                )                               AS payload_json,
                'PENDING'                       AS status,
                push.id_push_sub                AS push_sub_id,
                channels.delivery_channel       AS delivery_channel,
                channels.do_not_duplicate       AS do_not_duplicate,
                SHA2(CONCAT('NEW_GROUP_INVITE', '|', 'GROUP_INVITE', '|', inv.id_invite, '|', inv.recipient_id, '|', inv.roster_class), 256) AS sibling_key,
                NOW()                           AS created_at
            FROM group_invite inv
            JOIN group_listing grp ON inv.listing_id = grp.id_group
            JOIN users recipient ON inv.recipient_id = recipient.id_user
            JOIN users sender ON inv.sender_id = sender.id_user
            CROSS JOIN (
                SELECT 'IN_APP' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'DISCORD' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'PUSH' AS delivery_channel, NULL AS do_not_duplicate
            ) AS channels
            LEFT JOIN push_subscription push
                ON push.user_id = inv.recipient_id
                AND channels.delivery_channel = 'PUSH'
                AND push.social_notes_enabled = 1
            WHERE inv.id_invite = :inviteId
                AND (
                    channels.delivery_channel = 'IN_APP'
                    OR (channels.delivery_channel = 'DISCORD'
                        AND recipient.discord_user_id IS NOT NULL
                        AND recipient.external_social_notes_enabled = 1)
                    OR (channels.delivery_channel = 'PUSH'
                        AND push.social_notes_enabled = 1)
                )
            """, nativeQuery = true)
    int generateOutboxNotificationsForNewGroupInviteRequest(@Param("inviteId") Long inviteId);
}
