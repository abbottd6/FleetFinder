package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetMoonSystemServiceImpl implements PlanetMoonSystemService {

    private final PlanetMoonSystemRepository planetMoonSystemRepository;

    public PlanetMoonSystemServiceImpl(PlanetMoonSystemRepository planetMoonSystemRepository) {
        super();
        this.planetMoonSystemRepository = planetMoonSystemRepository;
    }

    @Override
    public List<PlanetMoonSystem> getAllPlanetMoonSystems() {
        return planetMoonSystemRepository.findAll();
    }

    @Override
    public PlanetMoonSystem getPlanetMoonSystemById(int id) {
        Optional<PlanetMoonSystem> optionalPlanetMoonSystem = planetMoonSystemRepository.findById(id);
        if (optionalPlanetMoonSystem.isPresent()) {
            return optionalPlanetMoonSystem.get();
        }
        else {
            throw new ResourceNotFoundException("PlanetMoonSystem with id " + id + " not found");
        }
    }
}
