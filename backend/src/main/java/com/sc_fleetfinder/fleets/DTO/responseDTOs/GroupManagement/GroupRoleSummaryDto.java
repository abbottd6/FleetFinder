package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupRoleSummaryDto {

    @NotNull(message = "GroupRoleSummaryDto field 'roleId' cannot be null")
    private Long roleId;

    @NotNull(message = "GroupRoleSummaryDto field 'roleTitle' cannot be null")
    private String roleTitle;

    private String roleCategory;
}
