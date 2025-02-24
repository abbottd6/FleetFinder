package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;

import java.util.List;

public interface GameEnvironmentService {

    List<GameEnvironmentDto> getAllEnvironments();
    GameEnvironmentDto getEnvironmentById(Integer id);
}
