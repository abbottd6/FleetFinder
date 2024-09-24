package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.GameEnvironment;

import java.util.List;

public interface GameEnvironmentService {

    List<GameEnvironment> getAllEnvironments();
    GameEnvironment getEnvironmentById(int id);
}
