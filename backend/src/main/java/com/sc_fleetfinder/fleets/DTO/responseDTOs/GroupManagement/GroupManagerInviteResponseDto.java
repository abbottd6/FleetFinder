package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInvitationStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupManagerInviteResponseDto {
    private Long inviteId;
    private Long listingId;
    private UserMonikerSummary senderSummary;
    private UserMonikerSummary recipientSummary;
    private GroupMemberStatus memberStatus;
    private GroupRoleSummaryDto roleSummary;
    private InviteDirection inviteDirection;
    private GroupInvitationStatus inviteStatus;
    private String inviteMessage;
    private Instant expiresAt;
    private Instant sentAt;
}
