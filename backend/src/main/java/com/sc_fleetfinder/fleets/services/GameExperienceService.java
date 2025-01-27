package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;

import java.util.List;

public interface GameExperienceService {

    List<GameExperienceDto> getAllExperiences();
    GameExperienceDto getExperienceById(Integer id);
}
