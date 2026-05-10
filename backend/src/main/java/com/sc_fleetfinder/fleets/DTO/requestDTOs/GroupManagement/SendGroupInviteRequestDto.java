package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendGroupInviteRequestDto {

    @NotNull(message="SendGroupInviteRequestDto field 'listingId' cannot be null.")
    private Long listingId;
    @NotBlank(message="SendGroupInviteRequestDto field 'inGameUsername' cannot be null.")
    private String inGameUsername;
    @NotNull(message="SendGroupInviteRequestDto field 'memberStatus' for roster class ('Active'/'Waitlist') cannot be null.")
    private GroupMemberStatus memberStatus;
    private String requestMessage;
    @NotNull(message="SendGroupInviteRequestDto field 'hasMic' cannot be null, should default to false.")
    private Boolean hasMic;
    @NotNull(message="SendGroupInviteRequestDto field 'hasHeadset' cannot be null, should default to false.")
    private Boolean hasHeadset;
}
