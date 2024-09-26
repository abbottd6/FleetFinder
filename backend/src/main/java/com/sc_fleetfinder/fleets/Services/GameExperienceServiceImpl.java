package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameExperienceServiceImpl implements GameExperienceService {

    private final ExperienceRepository experienceRepository;

    public GameExperienceServiceImpl(ExperienceRepository experienceRepository) {
        super();
        this.experienceRepository = experienceRepository;
    }

    @Override
    public List<GameExperience> getAllExperiences() {
        return experienceRepository.findAll();
    }

    @Override
    public GameExperience getExperienceById(int id) {
        Optional<GameExperience> experience = experienceRepository.findById(id);
        if (experience.isPresent()) {
            return experience.get();
        }
        else {
            throw new ResourceNotFoundException("Experience with id " + id + " not found");
        }
    }
}
