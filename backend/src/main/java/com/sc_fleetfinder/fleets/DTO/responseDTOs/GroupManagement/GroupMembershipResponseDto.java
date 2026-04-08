package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RsvpStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupMembershipResponseDto {

    private UserSummaryResponseDto userSummary;
    private GroupMemberStatus memberStatus;
    private MemberPositionSummaryDto memberRole;
    private GroupRankDto memberRank;
    private String memberNote;
    private Boolean hasComms;
    private Boolean hasExtNotes;
    private RsvpStatus rsvpStatus;
    private Instant joinedAt;
    private Boolean isAuthorizedManager;
    private GroupListingResponseDto listing;
}
