package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadTotalDto;

public interface ChatWsService {

    void updateConversationLastRead(String userSub, ChatReadDto chatReadDto);
    UserUnreadTotalDto updateUserUnreadTotal(String userSub);
}
