package com.sc_fleetfinder.fleets.DAO.Chat;

import com.sc_fleetfinder.fleets.entities.Chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
