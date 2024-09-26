package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;

import java.util.List;

public interface LegalityService {

    List<LegalityDto> getAllLegalities();
    LegalityDto getLegalityById(int id);
}
