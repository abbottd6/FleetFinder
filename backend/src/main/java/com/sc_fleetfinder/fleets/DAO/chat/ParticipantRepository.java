package com.sc_fleetfinder.fleets.DAO.chat;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ParticipantRepository extends JpaRepository<Participant, ConversationParticipantId> {

    @Query("""
            SELECT p FROM Participant p
            WHERE p.user.userId = :userId AND p.conversation.conversationId = :convId
            """)
    Optional<Participant> findByConversationAndUser(@Param("convId") Long convId, @Param("userId") Long userId);

    @Query("""
            SELECT p FROM Participant p
            WHERE p.conversation.conversationId = :convId
            AND p.user.userId IN :userIds
            """)
    List<Participant> findParticipants(@Param("convId") Long conversationId, @Param("userIds") Collection<Long> userIds);

    @Query("""
            SELECT p.conversation FROM Participant p
            WHERE p.user = :user AND p.isMuting = false AND p.isArchived = false
            ORDER BY p.conversation.updatedAt desc
            """)
    Page<Conversation> pageConversationsByUserParticipant(@Param("user") Users user, Pageable pageable);

    @Query("""
            SELECT p FROM Participant p
            WHERE p.user.userId = :userId AND p.isArchived = false AND p.isMuting = false
            """)
    Set<Participant> findParticipantRecordsByUserId(@Param("userId") Long userId);


    @Query("""
            SELECT new com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap(
                p.conversation.conversationId,
                COUNT(m)
            )
            FROM Participant p
                LEFT JOIN p.lastReadMessage lastRead
                LEFT JOIN Message m
                    ON m.conversation = p.conversation
                    AND m.sender.userId <> :userId
                    AND (lastRead IS NULL OR m.msgId > lastRead.msgId)
            WHERE p.user.userId = :userId AND p.isMuting = false
            GROUP BY p.conversation.conversationId
            """)
    List<ConvUnreadMap> userUnreadCountByUserId(@Param("userId") Long userId);
}
