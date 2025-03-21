package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;

import java.util.List;

public interface LegalityService {

    List<LegalityDto> getAllLegalities();
    LegalityDto getLegalityById(Integer id);
}
