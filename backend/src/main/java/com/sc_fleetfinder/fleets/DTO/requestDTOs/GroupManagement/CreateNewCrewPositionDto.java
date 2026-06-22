package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import lombok.Data;

@Data
public class CreateNewCrewPositionDto {

    private Long groupId;
    private Long subgroupId;
    private Long roleId;
    private String positionNote;
}
