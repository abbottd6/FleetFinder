package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
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
    private final ModelMapper modelMapper;

    public GameExperienceServiceImpl(ExperienceRepository experienceRepository) {
        super();
        this.experienceRepository = experienceRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "experienceCache", key = "'allExperiencesCache'")
    public List<GameExperienceDto> getAllExperiences() {
        log.info("Caching test: getting all game experiences");
        List<GameExperience> experiences = experienceRepository.findAll();

        if(experiences.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for game experiences");
        }

        return experiences.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameExperienceDto getExperienceById(Integer id) {
        Optional<GameExperience> experience = experienceRepository.findById(id);
        if (experience.isPresent()) {
            return convertToDto(experience.get());
        }
        else {
            throw new ResourceNotFoundException(id);
        }
    }

    public GameExperienceDto convertToDto(GameExperience entity) {
        //id valid check
        if(entity.getExperienceId() == null || entity.getExperienceId() == 0) {
            throw new ResourceNotFoundException("Experience id is null or empty");
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
