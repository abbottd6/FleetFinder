package com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement;

import com.sc_fleetfinder.fleets.utils.GroupManagement.CrewTemplateCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CrewTemplateSummaryDto {

    @NotNull(message="CrewTemplateSummaryDto field 'templateId' cannot be null.")
    private Long templateId;

    @NotNull(message="CrewTemplateSummaryDto field 'templateLabel' cannot be null.")
    private String templateLabel;

    @NotNull(message="CrewTemplateSummaryDto field 'templateCategory' cannot be null.")
    private CrewTemplateCategory templateCategory;

    private Long ownerId;
    private String ownerUsername;

    private Instant lastUsedAt;
}
