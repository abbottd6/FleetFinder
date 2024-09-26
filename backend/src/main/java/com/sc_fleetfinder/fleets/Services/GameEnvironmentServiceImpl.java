package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameEnvironmentServiceImpl implements GameEnvironmentService {


    private final EnvironmentRepository environmentRepository;

    public GameEnvironmentServiceImpl(EnvironmentRepository environmentRepository) {
        super();
        this.environmentRepository = environmentRepository;
    }

    @Override
    public List<GameEnvironment> getAllEnvironments() {
        return environmentRepository.findAll();
    }

    @Override
    public GameEnvironment getEnvironmentById(int id) {
        Optional<GameEnvironment> environment = environmentRepository.findById(id);
        if (environment.isPresent()) {
            return environment.get();
        }
        else {
            throw new ResourceNotFoundException("Game environment with id " + id + " not found");
        }
    }

}
