package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.websocketDTOs.ChatReadDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadResponseDto;
import com.sc_fleetfinder.fleets.services.chat_services.ChatWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class ChatWebSocketController {

    private final ChatWsService chatWsService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatWsService chatWsService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.chatWsService = chatWsService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.read")
    public void markRead(@Payload ChatReadDto dto, Principal principal) {
        String userSub = principal.getName();

        this.chatWsService.updateConversationLastRead(userSub, dto);

        UserUnreadResponseDto response = this.chatWsService.updateUserUnread(userSub);

        messagingTemplate.convertAndSendToUser(
                userSub,
                "/queue/chat.unread",
                response
        );
    }

    @MessageMapping("/chat.total_unread")
    public void getUnreadTotal(Principal principal) {
        String userSub = principal.getName();

        UserUnreadResponseDto dto = this.chatWsService.updateUserUnread(userSub);

        messagingTemplate.convertAndSendToUser(
                userSub,
                "/queue/chat.unread",
                dto
        );
    }
}
