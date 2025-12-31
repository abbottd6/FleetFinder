package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.GetConvMessagesRqstDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.chat_services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sc_fleetfinder.fleets.entities.Users;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("isAuthenticated() and hasRole('user')")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @PostMapping("/my_conversations")
    public Page<GetConversationDto> getMyConversations(@AuthenticationPrincipal Jwt jwt,
                                                       @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return chatService.findMyConversations(user, pageable);
    }

    @PostMapping("/conv_messages")
    public Page<GetMessageDto> getConversationMessages(@AuthenticationPrincipal Jwt jwt,
                                                       @RequestBody GetConvMessagesRqstDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Long convId = dto.getConversationId();

        Pageable pageable = PageRequest.of(dto.getPageIdx(), dto.getPageSize());

        return chatService.findConvMessages(user, convId, pageable);
    }

    @PostMapping("/conv_provision")
    public ResponseEntity<?> conversationProvision(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody FindOrStartNewConversationDto dto) {

        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return ResponseEntity.ok(chatService.findOrStartNew(user, dto));
    }

    @PostMapping("/send_message")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody SendMessageDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        GetMessageDto created = chatService.sendNewMessage(user, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
