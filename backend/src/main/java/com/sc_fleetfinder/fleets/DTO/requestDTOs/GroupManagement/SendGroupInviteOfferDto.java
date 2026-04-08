package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupRoleSummaryDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.UserMonikerSummary;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class SendGroupInviteOfferDto {

    @NotNull(message="SendGroupInviteOfferDto field 'listingId' cannot be null.")
    private Long listingId;
    private UserMonikerSummary recipientSummary;
    private GroupMemberStatus memberStatus;
    private GroupRoleSummaryDto roleSummary;
    private String inviteMessage;
    private Instant expiresAt;
}
