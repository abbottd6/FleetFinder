package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class GroupCompositionSubgroupDto {

    private Long subgroupId;
    private String subgroupLabel;
    private String subgroupNotes;
    private Integer sortOrder;
    private Long listingId;
    private Long rootSubgroupId;
    private Long parentSubgroupId;
    private Instant createdAt;
    private List<GroupCompositionSubgroupDto> subgroups;
    private List<GroupCompositionCrewPositionDto> crewPositions;
}
