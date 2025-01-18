package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
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
public class GameEnvironmentServiceImpl implements GameEnvironmentService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final EnvironmentRepository environmentRepository;
    private final ModelMapper modelMapper;

    public GameEnvironmentServiceImpl(EnvironmentRepository environmentRepository) {
        super();
        this.environmentRepository = environmentRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "environmentsCache", key = "'allEnvironmentsCache'")
    public List<GameEnvironmentDto> getAllEnvironments() {
        log.info("Caching test: getting all game environments");
        List<GameEnvironment> environments = environmentRepository.findAll();
        return environments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameEnvironmentDto getEnvironmentById(int id) {
        Optional<GameEnvironment> environment = environmentRepository.findById(id);
        if (environment.isPresent()) {
            return convertToDto(environment.get());
        }
        else {
            throw new ResourceNotFoundException("Game environment with id " + id + " not found");
        }
    }

    public GameEnvironmentDto convertToDto(GameEnvironment entity) {
        return modelMapper.map(entity, GameEnvironmentDto.class);
    }

    public GameEnvironment convertToEntity(GameEnvironmentDto dto) {
        return modelMapper.map(dto, GameEnvironment.class);
    }
}
