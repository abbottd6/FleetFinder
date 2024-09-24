package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetarySystemServiceImpl implements PlanetarySystemService {

    private final PlanetarySystemRepository planetarySystemRepository;

    public PlanetarySystemServiceImpl(PlanetarySystemRepository planetarySystemRepository) {
        super();
        this.planetarySystemRepository = planetarySystemRepository;
    }

    @Override
    public List<PlanetarySystem> getAllPlanetarySystems() {
        return planetarySystemRepository.findAll();
    }

    @Override
    public PlanetarySystem getPlanetarySystemById(int id) {
        Optional<PlanetarySystem> planetarySystem = planetarySystemRepository.findById(id);
        if(planetarySystem.isPresent()) {
            return planetarySystem.get();
        }
        else {
            throw new ResourceNotFoundException("Planetary system with id " + id + " not found");
        }
    }
}
