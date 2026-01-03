package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.hibernate.Hibernate.map;

@Service
public class ChatWsServiceImpl implements ChatWsService {

    private final UserRepository userRepo;
    private final ParticipantRepository participantRepo;
    private final MessageRepository messageRepo;

    ChatWsServiceImpl(UserRepository userRepo,
                      ParticipantRepository participantRepo,
                      MessageRepository messageRepo) {
        this.userRepo = userRepo;
        this.participantRepo = participantRepo;
        this.messageRepo = messageRepo;
    }

    @Override
    @Transactional
    public void updateConversationLastRead(String kcId, ChatReadDto dto) {
        Users userAnalog = this.userRepo.findByKeycloakId(kcId)
                .orElseThrow(() -> new ResourceNotFoundException("User with KeycloakId " +
                        kcId + " not found."));

        Message lastRead = this.messageRepo.findMessageByConversationIdAndMessageId(
                dto.conversationId(), dto.lastReadMsgId())
                .orElseThrow(() -> new ResourceNotFoundException("Message with ID: " +
                        dto.lastReadMsgId() + " and conversation Id" +
                        dto.conversationId() + " could not be found."));

        Participant part = this.participantRepo.findByConversationAndUser(dto.conversationId(), userAnalog.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant of " +
                        "conversation " + dto.conversationId() + ", with " +
                        "userId " + userAnalog.getUserId() + " not found."));

        part.setLastReadMessage(lastRead);
        part.setLastActiveAt(Instant.now());
        this.participantRepo.save(part);
    }
}
