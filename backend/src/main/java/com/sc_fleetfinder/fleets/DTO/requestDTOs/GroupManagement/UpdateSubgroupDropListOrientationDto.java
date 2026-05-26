package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.SubgroupDropListOrientation;
import lombok.Data;

@Data
public class UpdateSubgroupDropListOrientationDto {

    private Long groupId;
    private Long subgroupId;
    private SubgroupDropListOrientation orientation;
}
