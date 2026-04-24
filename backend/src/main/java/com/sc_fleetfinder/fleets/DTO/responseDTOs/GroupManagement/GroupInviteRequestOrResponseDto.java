package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInviteStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupInviteRequestOrResponseDto {

    private Long inviteId;
    private UserMonikerSummary senderSummary;
    private UserMonikerSummary recipientSummary;
    private GroupListingResponseDto listingDetails;
    private GroupMemberStatus memberStatus;
    private GroupRoleSummaryDto roleSummary;
    private InviteDirection inviteDirection;
    private GroupInviteStatus inviteStatus;
    private String inviteMessage;
    private Instant expiresAt;
    private Instant sentAt;
}
