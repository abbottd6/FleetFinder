package com.sc_fleetfinder.fleets.services.chat_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.UnMuteAndProvisionRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.DTO.websocketDTOs.UserUnreadResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

    Page<GetConversationDto> findMyConversations(Users user, Pageable pageable);
    Page<GetMessageDto> findConvMessages(Users user, Long convId, Pageable pageable);
    GetConversationDto findOrStartNew(Users user, FindOrStartNewConversationDto dto);
    GetMessageDto sendNewMessage(Users user, SendMessageDto dto);
    Conversation generateNewConversation(Users user, FindOrStartNewConversationDto dto, String dmKey);
    UserUnreadResponseDto getUserUnreadCounts(Long userId);
    void archiveConv(Users user, Long convId);
    void muteConv(Users user, Long convId);
    FindOrStartNewConversationDto unMuteConversation(Users user, UnMuteAndProvisionRequestDto dto);
}
