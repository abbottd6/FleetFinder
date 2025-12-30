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

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, ConversationParticipantId> {

    @Query("SELECT p FROM Participant p WHERE p.user.userId = :userId and p.conversation.conversationId = :convId")
    Optional<Participant> findByConversationAndUser(@Param("userId") Long userId, @Param("convId") Long convId);

    @Query("SELECT p.conversation FROM Participant p WHERE p.user = :user order by p.conversation.updatedAt asc")
    Page<Conversation> findConversationsByUserParticipant(@Param("user") Users user, Pageable pageable);
}
