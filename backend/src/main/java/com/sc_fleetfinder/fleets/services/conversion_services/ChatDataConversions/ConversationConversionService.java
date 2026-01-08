package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;

public interface ConversationConversionService {

    GetConversationDto convertToDto(Conversation entity, Long currentUserId);
    Conversation convertToEntity(GetConversationDto dto);
}
