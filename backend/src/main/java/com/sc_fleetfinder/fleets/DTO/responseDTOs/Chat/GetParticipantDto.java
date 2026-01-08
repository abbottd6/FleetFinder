package com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat;

import lombok.Data;

import java.time.Instant;

@Data
public class GetParticipantDto {
    private Long conversationId;
    private Long userId;
    private String role;
    private Instant joinedAt;
    private Instant leftAt;
    private Long lastReadMessageId;
    private Instant lastActiveAt;
    private boolean isArchived;
    private boolean isMuting;
    private Long deletedUpToMessage;
}
