package com.sc_fleetfinder.fleets.DAO.Chat;

import com.sc_fleetfinder.fleets.entities.Chat.Participant;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, ConversationParticipantId> {
}
