package com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat;

import lombok.Data;

import java.time.Instant;

@Data
public class GetConversationDto {
    private Long conversationId;
    private String conversationType;
    private String conversationTitle;
    private String initiatingUserName;
    private Long initiatingUserId;
    private Instant createdAt;
    private Instant updatedAt;
    private Long lastMsgId;
    private Long lastSenderUserId;
    private String lastSenderUserName;
    private String lastMsgBody;
    private String dmKey;
}
