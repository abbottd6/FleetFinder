package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ExperienceCachingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameExperienceServiceImpl implements GameExperienceService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final ExperienceRepository experienceRepository;
    private final ExperienceCachingService experienceCachingService;
    private final ModelMapper modelMapper;

    public GameExperienceServiceImpl(ExperienceRepository experienceRepository,
                                     ExperienceCachingService experienceCachingService) {
        super();
        this.experienceCachingService = experienceCachingService;
        this.experienceRepository = experienceRepository;
        this.modelMapper = new ModelMapper();
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
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public GameExperienceDto convertToDto(GameExperience entity) {
        //id valid check
        if(entity.getExperienceId() == null || entity.getExperienceId() == 0) {
            throw new ResourceNotFoundException("Experience id is null or 0");
        }

        //type/name valid check
        if(entity.getExperienceType() == null || entity.getExperienceType().isEmpty()) {
            throw new ResourceNotFoundException("Experience type is null or empty");
        }

        return modelMapper.map(entity, GameExperienceDto.class);
    }

    public GameExperience convertToEntity(GameExperienceDto dto) {
        //checking repository for entity matching Dto id
        GameExperience entity = experienceRepository.findById(dto.getExperienceId())
                .orElseThrow(() -> new ResourceNotFoundException("Experience with ID: " + dto.getExperienceId()
                        + " not found"));
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getExperienceType(), entity.getExperienceType())) {
            throw new ResourceNotFoundException("Experience type mismatch for Dto: " + dto.getExperienceType()
                    + ", ID: " + dto.getExperienceId() + " and entity: " + entity.getExperienceType()
                    + ", ID: " + entity.getExperienceId());
        }
        //if Id exists and names match then returns entity
        return entity;
    }
}
