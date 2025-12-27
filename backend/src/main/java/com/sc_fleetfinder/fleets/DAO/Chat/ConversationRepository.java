package com.sc_fleetfinder.fleets.DAO.Chat;

import com.sc_fleetfinder.fleets.entities.Chat.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
