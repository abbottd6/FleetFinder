package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;

import java.util.List;

public interface GameEnvironmentService {

    List<GameEnvironmentDto> getAllEnvironments();
    GameEnvironmentDto getEnvironmentById(int id);
}
