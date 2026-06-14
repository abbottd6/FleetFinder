package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlattenedGroupCompDto {

    public FlattenedGroupCompDto(Long groupId) {
        this.groupId = groupId;
    }

    private Long groupId;
    private List<GroupCompositionSubgroupDto> subgroups = new ArrayList<>();
    private List<GroupCompositionCrewPositionDto> crewPositions = new ArrayList<>();
}
