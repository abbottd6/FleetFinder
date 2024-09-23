package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import lombok.Data;

import java.util.Set;

@Data
public class PlanetarySystemDTO {

    private int systemId;
    private String systemName;
    private Set<PlanetMoonSystem> planetMoonSystems;
}
