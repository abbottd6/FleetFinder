package com.sc_fleetfinder.fleets.DAO.chat;

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
    Page<Conversation> findConversationsByUserParticipant(@Param("user") Users user, Pageable pageable);
}
