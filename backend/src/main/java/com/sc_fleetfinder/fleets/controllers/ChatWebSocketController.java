package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.services.chat_services.ChatWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class ChatWebSocketController {

    private final ChatWsService chatWsService;

    public ChatWebSocketController(ChatWsService chatWsService) {
        this.chatWsService = chatWsService;
    }

    @MessageMapping("/chat.read")
    public void markRead(@Payload ChatReadDto dto, Principal principal) {
        String userSub = principal.getName();

        this.chatWsService.updateConversationLastRead(userSub, dto);
    }
}
