package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ExperienceCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameExperienceServiceImpl implements GameExperienceService {

    private static final Logger log = LoggerFactory.getLogger(GameExperienceServiceImpl.class);
    private final ExperienceCachingService experienceCachingService;

    public GameExperienceServiceImpl(ExperienceCachingService experienceCachingService) {
        this.experienceCachingService = experienceCachingService;
    }

    @Override
    public List<GameExperienceDto> getAllExperiences() {
        return experienceCachingService.cacheAllExperiences();
    }

    @Override
    public GameExperienceDto getExperienceById(Integer id) {
        List<GameExperienceDto> cachedExperiences = experienceCachingService.cacheAllExperiences();

        return cachedExperiences.stream()
                .filter(experience -> experience.getExperienceId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Unable to find experience with id {}", id);
                    return new ResourceNotFoundException(id);
                });
    }
}
