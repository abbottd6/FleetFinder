package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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

        if (environments.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access game environments data");
        }
        return environments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameEnvironmentDto getEnvironmentById(Integer id) {
        Optional<GameEnvironment> environment = environmentRepository.findById(id);
        if (environment.isPresent()) {
            return convertToDto(environment.get());
        }
        else {
            throw new ResourceNotFoundException(id);
        }
    }

    public GameEnvironmentDto convertToDto(GameEnvironment entity) {
        return modelMapper.map(entity, GameEnvironmentDto.class);
    }

    public GameEnvironment convertToEntity(GameEnvironmentDto dto) {
        return modelMapper.map(dto, GameEnvironment.class);
    }
}
