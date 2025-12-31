package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ConversationConversionServiceImpl implements ConversationConversionService {

    private final ModelMapper conversationMapper;

    ConversationConversionServiceImpl(ModelMapper conversationMapper) {
        this.conversationMapper = conversationMapper;
    }

    @Override
    public GetConversationDto convertToDto(Conversation entity) {
        return conversationMapper.map(entity, GetConversationDto.class);
    }

    @Override
    public Conversation convertToEntity(GetConversationDto dto) {
        return conversationMapper.map(dto, Conversation.class);
    }
}
