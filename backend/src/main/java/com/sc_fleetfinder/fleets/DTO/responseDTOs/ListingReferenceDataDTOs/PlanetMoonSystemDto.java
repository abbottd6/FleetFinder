package com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs;

import lombok.Data;

@Data
public class PlanetMoonSystemDto {

    private Integer planetId;
    private String planetName;
    private Integer systemId;
    private String systemName;
}
