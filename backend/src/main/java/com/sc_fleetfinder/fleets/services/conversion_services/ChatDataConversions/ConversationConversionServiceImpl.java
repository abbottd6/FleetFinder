package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversationConversionServiceImpl implements ConversationConversionService {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public GetConversationDto convertToDto(Conversation entity) {
        return modelMapper.map(entity, GetConversationDto.class);
    }

    @Override
    public Conversation convertToEntity(GetConversationDto dto) {
        return modelMapper.map(dto, Conversation.class);
    }
}
