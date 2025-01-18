package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return experiences.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameExperienceDto getExperienceById(int id) {
        Optional<GameExperience> experience = experienceRepository.findById(id);
        if (experience.isPresent()) {
            return convertToDto(experience.get());
        }
        else {
            throw new ResourceNotFoundException("Experience with id " + id + " not found");
        }
    }

    public GameExperienceDto convertToDto(GameExperience entity) {
        return modelMapper.map(entity, GameExperienceDto.class);
    }

    public GameExperience convertToEntity(GameEnvironmentDto dto) {
        return modelMapper.map(dto, GameExperience.class);
    }
}
