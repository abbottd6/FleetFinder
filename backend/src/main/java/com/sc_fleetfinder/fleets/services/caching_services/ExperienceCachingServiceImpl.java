package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameExperienceConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceCachingServiceImpl implements ExperienceCachingService {

    private static final Logger log = LoggerFactory.getLogger(ExperienceCachingServiceImpl.class);
    private final ExperienceRepository experienceRepository;
    private final GameExperienceConversionService gameExperienceConversionService;

    @Autowired
    public ExperienceCachingServiceImpl(ExperienceRepository environmentRepository,
                                        GameExperienceConversionService gameExperienceConversionService) {
        this.experienceRepository = environmentRepository;
        this.gameExperienceConversionService = gameExperienceConversionService;
    }

    @Override
    @Cacheable(value = "experienceCache", key = "'allExperiencesCache'")
    public List<GameExperienceDto> cacheAllExperiences() {
        List<GameExperience> experiences = experienceRepository.findAll();

        if(experiences.isEmpty()) {
            log.error("Unable to access Experience data for caching.");
            throw new ResourceNotFoundException("Unable to access data for game experiences");
        }

        return experiences.stream()
                .map(gameExperienceConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
