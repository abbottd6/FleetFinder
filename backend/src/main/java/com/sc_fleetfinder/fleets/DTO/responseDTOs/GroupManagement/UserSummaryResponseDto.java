package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Data;

import java.time.Instant;

@Data
public class UserSummaryResponseDto {

    private Long userId;
    private String username;
    private String inGameUsername;
    private String discordUsername;
    private Instant lastAccess;
}
