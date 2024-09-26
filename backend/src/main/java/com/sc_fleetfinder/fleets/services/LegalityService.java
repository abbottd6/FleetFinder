package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.LegalityDto;

import java.util.List;

public interface LegalityService {

    List<LegalityDto> getAllLegalities();
    LegalityDto getLegalityById(int id);
}
