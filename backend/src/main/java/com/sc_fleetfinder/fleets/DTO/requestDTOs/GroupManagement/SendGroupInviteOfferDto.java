package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupRoleSummaryDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.UserMonikerSummary;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendGroupInviteOfferDto {

    //Convert Join Request to waitlist offer constructor
    public SendGroupInviteOfferDto(GroupInvite invite,
                            GroupMemberStatus rosterOffer,
                            UserMonikerSummary recipientSummary,
                            GroupRoleSummaryDto roleSummary,
                            String message) {
        this.listingId = invite.getGroupListing().getGroupId();
        this.recipientSummary = recipientSummary;
        this.memberStatus = rosterOffer;
        this.roleSummary = roleSummary;
        this.inviteMessage = message;
        this.expiresAt = null;
    }

    @NotNull(message="SendGroupInviteOfferDto field 'listingId' cannot be null.")
    private Long listingId;
    private UserMonikerSummary recipientSummary;
    private GroupMemberStatus memberStatus;
    private GroupRoleSummaryDto roleSummary;
    private String inviteMessage;
    private Instant expiresAt;
}
