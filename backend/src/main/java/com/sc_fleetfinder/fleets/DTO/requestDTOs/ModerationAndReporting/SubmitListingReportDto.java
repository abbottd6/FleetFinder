package com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitListingReportDto {

    @NotNull(message="SubmitListingReportDto field 'groupId' cannot be null.")
    private Long groupId;

    @NotNull(message="SubmitListingReportDto field 'reportBasis' cannot be null.")
    private Integer reportBasis;
}
