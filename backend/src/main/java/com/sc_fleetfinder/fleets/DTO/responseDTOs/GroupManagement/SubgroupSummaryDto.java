package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.Data;

import java.time.Instant;

@Data
public class SubgroupSummaryDto {

    private Long subgroupId;
    private String subgroupLabel;
    private String subgroupNotes;
    private Long parentSubgroupId;
    private String parentSubgroupLabel;
    private Integer intendedSubgroupSize;
    private Instant createdAt;
}
