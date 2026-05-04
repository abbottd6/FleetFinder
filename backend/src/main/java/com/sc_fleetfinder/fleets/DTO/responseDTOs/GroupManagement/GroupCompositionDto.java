package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupCompositionDto {

    private List<GroupCompositionSubgroupDto> subgroups;
}
