package com.sc_fleetfinder.fleets.DTO.requestDTOs.chat;

import com.sc_fleetfinder.fleets.utils.ConversationType;
import lombok.Data;

@Data
public class FindOrStartNewConversationDto {

    public FindOrStartNewConversationDto(ConversationType convType,
                                  String title,
                                  Long recipientId) {
        this.convType = convType;
        this.title = title;
        this.recipientId = recipientId;
    }

    private ConversationType convType;
    private String title;
    private Long recipientId;
}
