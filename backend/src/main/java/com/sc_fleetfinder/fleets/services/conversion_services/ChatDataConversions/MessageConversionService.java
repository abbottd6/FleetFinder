package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.chat.Message;

public interface MessageConversionService {
    GetMessageDto convertToDto(Message entity);

}
