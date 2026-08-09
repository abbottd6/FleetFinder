package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupRoleSummaryDto;
import com.sc_fleetfinder.fleets.utils.GroupManagement.SubgroupDropListOrientation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddNewSubgroupRequestDto {

    @NotNull(message = "AddNewSubgroupRequestDto field 'listingId' cannot be null.")
    private Long listingId;

    private Long rootSubgroupId;
    private Long parentSubgroupId;
    private String subgroupLabel;
    private String subgroupNotes;
    private SubgroupDropListOrientation dropListOrientation;
    private GroupRoleSummaryDto[] positions;
}
