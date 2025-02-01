package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;

import java.util.List;

public interface LegalityService {

    List<LegalityDto> getAllLegalities();
    LegalityDto getLegalityById(Integer id);
    LegalityDto convertToDto(Legality entity);
}
