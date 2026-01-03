package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;

public interface ChatWsService {

    void updateConversationLastRead(String userSub, ChatReadDto chatReadDto);
}
