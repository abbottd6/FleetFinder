package com.sc_fleetfinder.fleets.DTO.requestDTOs.chat;

import com.sc_fleetfinder.fleets.utils.ConversationType;
import lombok.Data;

@Data
public class FindOrStartNewConversationDto {

    private ConversationType convType;
    private String title;
    private Long recipientId;
}
