package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.GameEnvironmentDto;

import java.util.List;

public interface GameEnvironmentService {

    List<GameEnvironmentDto> getAllEnvironments();
    GameEnvironmentDto getEnvironmentById(int id);
}
