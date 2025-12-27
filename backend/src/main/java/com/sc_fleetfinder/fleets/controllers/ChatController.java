package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Messaging.GetConversationDto;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("isAuthenticated() and hasRole('user')")
public class ChatController {

//    @PostMapping("/my_conversations")
//    public Page<GetConversationDto> getMyConversations(@AuthenticationPrincipal Jwt jwt,
//                                                       @RequestBody SortablePageRequestDto pageDto) {
//
//    }
}
