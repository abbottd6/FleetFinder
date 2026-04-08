package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupRankDto {

    @NotNull(message="GroupRankDto field 'rankId' cannot be null.")
    private Long rankId;
    private Long listingId;
    private Long rankSubgroupScope;
    @NotNull(message="GroupRankDto field 'rankTitle' cannot be null.")
    private String rankTitle;
    private String rankNotes;
    private UserMonikerSummary createdByUser;
    private Instant createdAt;

}
