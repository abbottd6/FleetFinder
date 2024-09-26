package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PlanetarySystemDto {

    private int systemId;
    private String systemName;
    private Set<PlanetMoonSystemDto> planetMoonSystems = new HashSet<>();
}
