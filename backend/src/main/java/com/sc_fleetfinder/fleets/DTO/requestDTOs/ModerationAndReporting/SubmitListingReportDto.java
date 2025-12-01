package com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitListingReportDto {

    @NotNull(message="SubmitListingReportDto field 'groupId' cannot be null.")
    private Long groupId;

    private Users user;

    @NotNull(message="SubmitListingReportDto field 'reportBasis' cannot be null.")
    private Integer reportBasis;
}
