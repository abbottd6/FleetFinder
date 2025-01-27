package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PlanetarySystemDto {

    private Integer systemId;
    private String systemName;
    //private Set<PlanetMoonSystemDto> planetMoonSystems = new HashSet<>();
}
