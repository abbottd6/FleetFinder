package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Data;

import java.time.Instant;

@Data
public class GroupCompositionCrewPositionDto {

    private Long positionId;
    private Long groupId;
    private String groupTitle;
    private Long rootSubgroupId;
    private Long subgroupId;
    private String subgroupLabel;
    private Integer sortOrder;
    private GroupRoleSummaryDto groupRole;
    private String positionNote;
    private GroupManagerMemberResponseDto assignedMember;
    private Instant filledAt;
    private Instant vacatedAt;
    private Instant createdAt;
}
