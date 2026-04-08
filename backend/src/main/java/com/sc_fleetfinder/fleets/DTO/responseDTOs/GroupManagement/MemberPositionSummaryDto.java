package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Data;

import java.time.Instant;

@Data
public class MemberPositionSummaryDto {

    private Long positionId;
    private Long listingId;
    private SubgroupSummaryDto subgroupSummary;
    private GroupRoleSummaryDto roleSummary;
    private String positionNote;
    private Instant filledAt;
    private Instant createdAt;
}
