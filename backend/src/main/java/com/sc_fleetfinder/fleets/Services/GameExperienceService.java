package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.GameExperience;

import java.util.List;

public interface GameExperienceService {

    List<GameExperience> getAllExperiences();
    GameExperience getExperienceById(int id);
}
