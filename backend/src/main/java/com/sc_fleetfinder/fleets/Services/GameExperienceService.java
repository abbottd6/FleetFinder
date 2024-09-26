package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;

import java.util.List;

public interface GameExperienceService {

    List<GameExperienceDto> getAllExperiences();
    GameExperienceDto getExperienceById(int id);
}
