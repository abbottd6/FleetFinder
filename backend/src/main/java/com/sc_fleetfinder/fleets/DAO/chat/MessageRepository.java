package com.sc_fleetfinder.fleets.DAO.chat;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
