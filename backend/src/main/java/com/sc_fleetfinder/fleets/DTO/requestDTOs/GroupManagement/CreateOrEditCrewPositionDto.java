package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrEditCrewPositionDto {

    @NotNull(message = "CreateOrEditCrewPositionDto field 'groupId' cannot be null.")
    private Long groupId;
    @NotNull(message = "CreateOrEditCrewPositionDto field 'subgroupId' cannot be null.")
    private Long subgroupId;
    @NotNull(message = "CreateOrEditCrewPositionDto field 'roleId' cannot be null.")
    private Long roleId;
    private String positionNote;
}
