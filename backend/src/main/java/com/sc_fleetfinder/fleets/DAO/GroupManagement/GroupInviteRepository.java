package com.sc_fleetfinder.fleets.DAO.GroupManagement;

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

    @Modifying
    @Query("""
            UPDATE GroupInvite inv
            SET inv.inviteStatus = 'RESCINDED'
            WHERE inv.recipient = :recipient
                AND inv.groupListing.groupId = :listingId
                AND inv.inviteDirection = :dir
                AND inv.memberStatus = 'WAITLIST'
            """)
    int setExistingWaitlistInviteToRescinded (
            @Param("recipient") Users recip,
            @Param("listingId") Long listingId,
            @Param("dir") InviteDirection dir);

    @Query("""
            SELECT inv FROM GroupInvite inv
            WHERE (inv.inviteDirection = InviteDirection.OFFER
                        AND inv.recipient.userId = :userId
                        AND inv.recipientDismissed = false)
                  OR (inv.inviteDirection = InviteDirection.REQUEST
                        AND inv.sender.userId = :userId
                        AND inv.senderDismissed = false)
            ORDER BY inv.createdAt DESC
            """)
    Page<GroupInvite> findInvitesByUserId(@Param("userId") Long userId, Pageable pageable);

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
                AND((inv.inviteDirection = "REQUEST" AND inv.recipientDismissed = false)
                OR (inv.inviteDirection = "OFFER" AND inv.senderDismissed = false))
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
                    'noteTopic',            sender.user_name,
                    'targetId',             inv.sender_id,
                    'targetLabel',          inv.roster_class,
                    'targetStatus',         inv.invite_status,
                    'targetCreatedAt',      inv.created_at,
                    'contextElementLabel',  grp.listing_title,
                    'contextElementStatus', grpStatus.group_status,
                    'contextElementDate',   grp.event_schedule,
                    'addContext',           SUBSTRING(inv.invite_message, 0, 64)
                )                               AS payload_json,
                'PENDING'                       AS status,
                push.id_push_sub                AS push_sub_id,
                channels.delivery_channel       AS delivery_channel,
                channels.do_not_duplicate       AS do_not_duplicate,
                SHA2(CONCAT('NEW_GROUP_INVITE', '|', 'GROUP_INVITE', '|', inv.id_invite, '|', inv.recipient_id, '|', inv.roster_class), 256) AS sibling_key,
                NOW()                           AS created_at
            FROM group_invite inv
            JOIN group_listing grp ON inv.listing_id = grp.id_group
            JOIN group_status grpStatus ON grp.group_status_id = grpStatus.group_status_id
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
    int generateOutboxNotificationsForNewBidirectionalGroupInvite(@Param("inviteId") Long inviteId);

    @Modifying
    @Query(value = """
           INSERT IGNORE INTO notification_outbox (
            event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            parent_entity_id, parent_entity_type, payload_json, status, push_sub_id,
            delivery_channel, do_not_duplicate, sibling_key, created_at
            )
           SELECT
                :noteType                       AS event_type,
                'group_member'                  AS entity_type,
                :newMemberUserId                AS entity_id,
                :recipientId                    AS entity_owner_id,
                member.member_status            AS entity_new_status,
                :listingId                      AS parent_entity_id,
                'group_listing'                 AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',                SUBSTRING(listing.listing_title, 1, 64),
                    'targetId',                 :newMemberUserId,
                    'targetLabel',              u.user_name,
                    'targetStatus',             inv.invite_status,
                    'targetCreatedAt',          inv.created_at,
                    'contextElementLabel',      listing.listing_title,
                    'contextElementStatus',     grpStatus.group_status,
                    'contextElementDate',       listing.event_schedule,
                    'addContext',               inv.direction
                )                               AS payload_json,
                'PENDING'                       AS status,
                push.id_push_sub                AS push_sub_id,
                channels.delivery_channel       AS delivery_channel,
                channels.do_not_duplicate       AS do_not_duplicate,
                SHA2(CONCAT('NEW_GROUP_MEMBER', '|', 'group_member', :newMemberUserId, '|', :recipientId, '|', member.member_status), 256) AS sibling_key,
                NOW()                           AS created_at
           FROM users user
           JOIN group_member member ON member.user_id = :newMemberUserId
                AND member.listing_id = :listingId
           JOIN users u ON member.user_id = u.id_user
           JOIN group_listing listing ON member.listing_id = listing.id_group
           JOIN group_status grpStatus ON listing.group_status_id = grpStatus.group_status_id
           JOIN group_invite inv ON inv.id_invite = :inviteId
           LEFT JOIN crew_role_classification role ON role.id_role = inv.role_id
           CROSS JOIN (
                SELECT 'IN_APP' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'DISCORD' AS delivery_channel, 1 AS do_not_duplicate UNION ALL
                SELECT 'PUSH' AS delivery_channel, NULL AS do_not_duplicate
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
                    OR (channels.delivery_channel = 'PUSH')
                        AND push.group_notes_enabled = 1)
           """, nativeQuery = true)
    int generateOutboxNotificationsForNewGroupMember(@Param("recipientId") Long notificationRecipientId,
                                                     @Param("listingId") Long listingId,
                                                     @Param("newMemberUserId") Long newMemberUserId,
                                                     @Param("inviteId") Long inviteId,
                                                     @Param("noteType") String noteType);
}
