package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMonikerSummary {

    public UserMonikerSummary(Users user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.inGameUsername = user.getInGameUsername();
    }

    @NotNull(message="InviteUsersSummary dto field 'userId' cannot be null")
    private Long userId;

    @NotNull(message="InviteUsersSummary dto field 'username' cannot be null")
    private String username;

    private String inGameUsername;
}
