package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCompositionDto {

    public GroupCompositionDto(Long groupId) {
        this.groupId = groupId;
        subgroups = new ArrayList<>();
    }

    private Long groupId;
    private List<GroupCompositionSubgroupDto> subgroups = new ArrayList<>();
}
