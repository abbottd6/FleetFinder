package com.sc_fleetfinder.fleets.DTO.requestDTOs.chat;

import lombok.Data;

@Data
public class SendMessageDto {
    private Long conversationId;
    private Long senderId;
    private String messageType;
    private String msgBody;
    private Long repliedToMsgId;
    private String clientMessageId;
}
