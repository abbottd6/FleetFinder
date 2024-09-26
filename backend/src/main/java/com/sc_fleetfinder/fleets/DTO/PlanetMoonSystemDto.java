package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import lombok.Data;

@Data
public class PlanetMoonSystemDto {

    private int planetId;
    private String planetName;
    private String systemName;
}
