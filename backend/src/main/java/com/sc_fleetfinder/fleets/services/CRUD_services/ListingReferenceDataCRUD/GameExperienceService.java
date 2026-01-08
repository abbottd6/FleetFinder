package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameExperienceDto;

import java.util.List;

public interface GameExperienceService {

    List<GameExperienceDto> getAllExperiences();
    GameExperienceDto getExperienceById(Integer id);
}
