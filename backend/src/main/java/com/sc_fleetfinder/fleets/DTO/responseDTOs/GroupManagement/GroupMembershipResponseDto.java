package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.utils.GroupManagement.MemberStatusOptions;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RsvpStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupMembershipResponseDto {

    private UserSummaryResponseDto userSummary;
    private MemberStatusOptions memberStatus;
    private String memberNote;
    private Boolean hasComms;
    private Boolean hasExtNotes;
    private RsvpStatus rsvpStatus;
    private Instant joinedAt;
    private InGroupRank memberRank;
    private GroupListing listing;
}
