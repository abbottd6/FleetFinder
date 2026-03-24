package com.sc_fleetfinder.fleets.DAO.chat;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT msg FROM Message msg WHERE msg.conversation.conversationId = :convId order by msg.createdAt desc")
    Page<Message> findMessagesByConversationId(@Param("convId") Long convId, Pageable pageable);

    Long countMessagesByConversation_ConversationId(Long conversationId);

    @Query("""
            SELECT msg FROM Message msg
            WHERE msg.conversation.conversationId = :convId AND msg.msgId = :msgId
            """)
    Optional<Message> findMessageByConversationIdAndMessageId(@Param("convId") Long conversationId, @Param("msgId") Long messageId);

    @Modifying
    @Query(value = """
            INSERT IGNORE INTO notification_outbox (
            event_type, entity_type, entity_id, entity_owner_id, entity_new_status,
            parent_entity_id, parent_entity_type, payload_json, status,
            delivery_channel, created_at
            )
            SELECT
                'NEW_CHAT_MESSAGE'          AS event_type,
                'MESSAGE'                   AS entity_type,
                msg.id_msg                  AS entity_id,
                :recipientId                AS entity_owner_id,
                'NEW MESSAGE'               AS entity_new_status,
                conv.id_conversation        AS parent_entity_id,
                'CONVERSATION'              AS parent_entity_type,
                JSON_OBJECT(
                    'noteTopic',        conv.title,
                    'targetId',         conv.id_conversation,
                    'targetLabel',      SUBSTRING(msg.msg_body, 1, 100),
                    'targetCreatedAt',  msg.created_at,
                    'addContext',       'Social Notification'
                )                           AS payload_json,
                'PENDING'                   AS status,
                channels.delivery_channel   AS delivery_channel,
                NOW()                       AS created_at
            FROM message msg
            JOIN conversation conv ON msg.id_conversation = conv.id_conversation
            LEFT JOIN notification noti ON conv.id_conversation = noti.parent_entity_id
                AND noti.id_user = :recipientId
                AND noti.parent_entity_type = 'CONVERSATION'
            JOIN conversation_participant parti ON conv.id_conversation = parti.id_conversation
                AND parti.id_user = :recipientId
            JOIN users u ON :recipientId = u.id_user
            CROSS JOIN (
                SELECT 'DISCORD' AS delivery_channel UNION ALL
                SELECT 'PUSH'
            ) AS channels
            LEFT JOIN push_subscription push
                ON push.user_id = :recipientId
                AND channels.delivery_channel = 'PUSH'
                AND push.social_notes_enabled = 1
            WHERE msg.id_msg = :msgId
                AND (noti.created_at IS NULL OR noti.created_at < (NOW() - INTERVAL 5 MINUTE))
                AND (parti.last_read_message_id IS NULL OR parti.last_read_message_id < :msgId)
                AND (
                    (channels.delivery_channel = 'DISCORD'
                        AND u.discord_user_id IS NOT NULL
                        AND u.external_social_notes_enabled = 1)
                    OR (channels.delivery_channel = 'PUSH'
                        AND push.social_notes_enabled = 1)
                )
            """, nativeQuery = true)
    int generateExternalDeliveryOutboxNotifications(@Param("recipientId") Long recipientId, @Param("msgId") Long msgId);
}
