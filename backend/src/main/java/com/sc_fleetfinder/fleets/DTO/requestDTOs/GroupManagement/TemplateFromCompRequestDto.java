package com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TemplateFromCompRequestDto {
    @NotNull(message = "TemplateFromCompRequestDto field 'groupId' cannot be null")
    private Long groupId;
    @NotBlank(message = "TemplateFromCompRequestDto field 'templateLabel' cannot be empty")
    private String templateLabel;
    @NotEmpty(message = "TemplateFromCompRequestDto field 'subgroups' must contain at least one subgroup")
    private List<GroupCompositionSubgroupDto> subgroups;
}
