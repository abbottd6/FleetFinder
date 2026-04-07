package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRosterClass;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RsvpStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupManagerMemberResponseDto {
    private Long listingId;
    private UserSummaryResponseDto userSummary;
    private GroupRosterClass memberStatus;
    private String memberNote;
    private Boolean hasComms;
    private Boolean hasExtNotes;
    private RsvpStatus rsvpStatus;
    private Instant joinedAt;
    private InGroupRank memberRank;
}
