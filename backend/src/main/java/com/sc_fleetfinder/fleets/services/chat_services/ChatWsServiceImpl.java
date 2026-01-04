package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DAO.chat.MessageRepository;
import com.sc_fleetfinder.fleets.DAO.chat.ParticipantRepository;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ConvUnreadMap;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadPerConvDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadTotalDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
public class ChatWsServiceImpl implements ChatWsService {

    private final UserRepository userRepo;
    private final ParticipantRepository participantRepo;
    private final MessageRepository messageRepo;
    private final ChatService chatService;

    ChatWsServiceImpl(UserRepository userRepo,
                      ParticipantRepository participantRepo,
                      MessageRepository messageRepo,
                      ChatService chatService) {
        this.userRepo = userRepo;
        this.participantRepo = participantRepo;
        this.messageRepo = messageRepo;
        this.chatService = chatService;
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

        log.info("what happened to lastRead? {}", lastRead.getMsgId());

        Participant part = this.participantRepo.findByConversationAndUser(dto.conversationId(), userAnalog.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant of " +
                        "conversation " + dto.conversationId() + ", with " +
                        "userId " + userAnalog.getUserId() + " not found."));

        if(part.getLastReadMessage().getMsgId() >= lastRead.getMsgId()) {
            return;
        }

        part.setLastReadMessage(lastRead);
        part.setLastActiveAt(Instant.now());
        this.participantRepo.save(part);
    }

    @Override
    public UserUnreadTotalDto updateUserUnreadTotal(String kcId) {
        Users userAnalog = this.userRepo.findByKeycloakId(kcId)
                .orElseThrow(() -> new ResourceNotFoundException("User with KeycloakId " +
                        kcId + " not found."));

        UserUnreadPerConvDto unreadPerConv =
                this.chatService.getUserUnreadCountPerConversation(userAnalog.getUserId());

        long totalUnread = 0L;

        for(ConvUnreadMap conv : unreadPerConv.convIdAndUnread()) {
            totalUnread += conv.unreadCount();
        }

        return new UserUnreadTotalDto(userAnalog.getUserId(), totalUnread);
    }
}
