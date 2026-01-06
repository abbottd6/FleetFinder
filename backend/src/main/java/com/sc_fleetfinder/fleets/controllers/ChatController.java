package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.GetConvMessagesRqstDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.UnMuteAndProvisionRequestDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sc_fleetfinder.fleets.entities.Users;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("isAuthenticated() and hasRole('user')")
public class ChatController {

    public int DEF_CONV_PAGE_SIZE = 10;
    public int DEF_CONV_PAGE_IDX = 0;

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
    public GetConversationDto conversationProvision(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody FindOrStartNewConversationDto dto) {

        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return chatService.findOrStartNew(user, dto);
    }

    @PostMapping("/send_message")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody SendMessageDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        GetMessageDto created = chatService.sendNewMessage(user, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //todo archive conv returns conversations
    @PatchMapping("/archive_conv/{convId}")
    public Page<GetConversationDto> archiveConvAndReturnConvs(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Long convId,
                                                              @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        chatService.archiveConv(user, convId);

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return chatService.findMyConversations(user, pageable);
    }

    //todo mute conv returns conversations
    @PatchMapping("/mute_conv/{convId}")
    public Page<GetConversationDto> muteConvAndReturnConvs(@AuthenticationPrincipal Jwt jwt,
                                                           @PathVariable Long convId,
                                                           @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        chatService.muteConv(user, convId);

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return chatService.findMyConversations(user, pageable);
    }

    @PatchMapping("/unmute_conv")
    public GetConversationDto unmuteAndReturnConv(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody UnMuteAndProvisionRequestDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        FindOrStartNewConversationDto provDto = chatService.unMuteConversation(user, dto);

        return chatService.findOrStartNew(user, provDto);
    }
}
