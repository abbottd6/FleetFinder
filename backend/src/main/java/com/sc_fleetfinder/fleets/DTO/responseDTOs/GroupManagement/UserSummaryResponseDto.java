package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryResponseDto {

    private Long userId;
    private String username;
    private String inGameUsername;
    private String discordUsername;
    private Instant lastAccess;
}
