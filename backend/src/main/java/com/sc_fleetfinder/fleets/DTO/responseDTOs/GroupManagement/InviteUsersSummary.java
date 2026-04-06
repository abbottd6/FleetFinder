package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteUsersSummary {

    @NotNull(message="InviteUsersSummary dto field 'userId' cannot be null")
    private Long userId;

    @NotNull(message="InviteUsersSummary dto field 'username' cannot be null")
    private String username;

    private String inGameUsername;
}
