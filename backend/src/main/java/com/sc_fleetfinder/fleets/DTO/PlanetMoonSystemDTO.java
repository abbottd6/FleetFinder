package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import lombok.Data;

@Data
public class PlanetMoonSystemDTO {

    private int planetId;
    private int planetName;
    private PlanetarySystem planetarySystem;
}
