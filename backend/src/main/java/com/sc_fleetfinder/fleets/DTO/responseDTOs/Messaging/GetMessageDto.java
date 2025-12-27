package com.sc_fleetfinder.fleets.DTO.responseDTOs.Messaging;

import lombok.Data;

import java.time.Instant;

@Data
public class GetMessageDto {
    private Long msgId;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String messageType;
    private String msgBody;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long repliedToMessageId;
    private String clientMessageId;
}
