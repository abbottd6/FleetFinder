package com.sc_fleetfinder.fleets.DAO.chat;

import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByDmKey(String dmKey);
}
