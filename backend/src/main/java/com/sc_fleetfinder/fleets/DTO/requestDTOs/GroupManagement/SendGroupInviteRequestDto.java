package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRosterClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendGroupInviteRequestDto {

    @NotNull(message="SendGroupInviteRequestDto field 'listingId' cannot be null.")
    private Long listingId;
    @NotBlank(message="SendGroupInviteRequestDto field 'inGameUsername' cannot be null.")
    private String inGameUsername;
    private GroupRosterClass rosterClass;
    private String requestMessage;
}
