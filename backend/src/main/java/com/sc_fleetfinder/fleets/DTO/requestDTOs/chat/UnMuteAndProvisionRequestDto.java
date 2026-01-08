package com.sc_fleetfinder.fleets.DTO.requestDTOs.chat;

import com.sc_fleetfinder.fleets.utils.ConversationType;
import lombok.Data;

@Data
public class UnMuteAndProvisionRequestDto {
    private ConversationType convType;
    private String title;
    private Long recipientId;
    private String otherUsername;
    private Long conversationId;
}
