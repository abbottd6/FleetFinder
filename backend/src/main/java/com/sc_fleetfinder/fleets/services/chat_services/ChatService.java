package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ChatService {

    Page<GetConversationDto> findMyConversations(Users user, Pageable pageable);
    Page<GetMessageDto> findConvMessages(Users user, Long convId, Pageable pageable);
    GetConversationDto findOrStartNew(Users user, FindOrStartNewConversationDto dto);
    GetMessageDto sendNewMessage(Users user, SendMessageDto dto);
}
