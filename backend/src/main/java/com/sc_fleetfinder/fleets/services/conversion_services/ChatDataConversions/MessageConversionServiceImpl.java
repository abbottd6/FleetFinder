package com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConversionServiceImpl implements MessageConversionService {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public GetMessageDto convertToDto(Message entity) {
        return modelMapper.map(entity, GetMessageDto.class);
    }
}
