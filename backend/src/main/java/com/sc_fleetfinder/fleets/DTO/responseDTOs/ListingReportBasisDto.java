package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListingReportBasisDto {

    @NotNull(message="ListingReportBasisDto field 'basisId' cannot be null.")
    private Integer basisId;

    @NotNull(message="ListingReportBasisDto field 'basisLabel' cannot be null.")
    private String basisLabel;
}
