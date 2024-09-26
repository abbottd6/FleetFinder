package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.GameExperienceDto;

import java.util.List;

public interface GameExperienceService {

    List<GameExperienceDto> getAllExperiences();
    GameExperienceDto getExperienceById(int id);
}
