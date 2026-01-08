package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class MessageConversionServiceImpl implements MessageConversionService {

    private final ModelMapper messageMapper;

    MessageConversionServiceImpl(ModelMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public GetMessageDto convertToDto(Message entity) {
        return messageMapper.map(entity, GetMessageDto.class);
    }
}
