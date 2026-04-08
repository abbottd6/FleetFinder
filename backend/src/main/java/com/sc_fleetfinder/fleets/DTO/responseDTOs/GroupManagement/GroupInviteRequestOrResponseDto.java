package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInvitationStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRosterClass;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupInviteRequestOrResponseDto {

    private Long inviteId;
    private InviteUsersSummary senderSummary;
    private InviteUsersSummary recipientSummary;
    private GroupListingResponseDto listingDetails;
    private GroupRosterClass rosterClass;
    private GroupRoleSummaryDto roleSummary;
    private InviteDirection inviteDirection;
    private GroupInvitationStatus inviteStatus;
    private String inviteMessage;
    private Instant expiresAt;
    private Instant sentAt;
}
