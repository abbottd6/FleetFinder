package com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs;

import lombok.Data;

@Data
public class PlanetarySystemDto {

    private Integer systemId;
    private String systemName;
    //private Set<PlanetMoonSystemDto> planetMoonSystems = new HashSet<>();
}
