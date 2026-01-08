package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class ConversationConversionServiceImpl implements ConversationConversionService {

    ConversationConversionServiceImpl() {}

    @Override
    public GetConversationDto convertToDto(Conversation conv, Long currentUserId) {

        Users otherUser = conv.getParticipants().stream()
                .filter(participant -> !Objects.equals(
                        participant.getUser().getUserId(), currentUserId))
                .findFirst()
                .map(Participant::getUser)
                .orElseThrow(() -> new ConversationIntegrityException("Conversation with ID: " +
                        conv.getConversationId() + " is missing the other participant."));

        Users currentUser = conv.getParticipants().stream()
                .filter(participant -> Objects.equals(
                        participant.getUser().getUserId(), currentUserId))
                .findFirst()
                .map(Participant::getUser)
                .orElseThrow(() -> new ConversationIntegrityException("Conversation with ID: " +
                        conv.getConversationId() + " is missing the current participant."));

        return new GetConversationDto(conv, currentUser, otherUser);
    }

    @Override
    public Conversation convertToEntity(GetConversationDto dto) {
        log.error("Dayton has not implemented this method yet, it returns null.");
        return null;
    }
}
