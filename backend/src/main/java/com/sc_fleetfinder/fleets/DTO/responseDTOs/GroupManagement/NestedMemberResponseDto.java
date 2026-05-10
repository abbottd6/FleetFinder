package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RsvpStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class NestedMemberResponseDto {

    private Long listingId;
    private UserSummaryResponseDto userSummary;
    private GroupMemberStatus memberStatus;
    private String memberNote;
    private GroupRankDto memberRank;
    private Boolean hasMic;
    private Boolean hasHeadset;
    private Boolean hasExtNotes;
    private RsvpStatus rsvpStatus;
    private Instant joinedAt;
}
