package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.EnvironmentCachingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GameEnvironmentServiceImpl implements GameEnvironmentService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentCachingService environmentCachingService;
    private final ModelMapper modelMapper;

    public GameEnvironmentServiceImpl(EnvironmentRepository environmentRepository, EnvironmentCachingService environmentCachingService) {
        super();
        this.environmentRepository = environmentRepository;
        this.environmentCachingService = environmentCachingService;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<GameEnvironmentDto> getAllEnvironments() {
        return environmentCachingService.cacheAllEnvironments();
    }

    @Override
    public GameEnvironmentDto getEnvironmentById(Integer id) {
        List<GameEnvironmentDto> cachedEnvironments = environmentCachingService.cacheAllEnvironments();

        return cachedEnvironments.stream()
                .filter(environment -> environment.getEnvironmentId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public GameEnvironmentDto convertToDto(GameEnvironment entity) {

        if(entity.getEnvironmentId() == null || entity.getEnvironmentId() == 0) {
            throw new ResourceNotFoundException("Environment id is null or empty");
        }

        if(entity.getEnvironmentType() == null || entity.getEnvironmentType().isEmpty()) {
            throw new ResourceNotFoundException("Environment type is null or empty");
        }

        return modelMapper.map(entity, GameEnvironmentDto.class);
    }

    public GameEnvironment convertToEntity(GameEnvironmentDto dto) {

        //checking repository for entity matching Dto id
        GameEnvironment entity = environmentRepository.findById(dto.getEnvironmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Environment with ID: " + dto.getEnvironmentId()
                 + " not found"));
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getEnvironmentType(), entity.getEnvironmentType())) {
            throw new ResourceNotFoundException("Environment type mismatch for Dto: "
                    + dto.getEnvironmentType() + ", ID: " + dto.getEnvironmentId() + " and entity: "
                    + entity.getEnvironmentId() + ", ID: " + entity.getEnvironmentType());
        }
        //if Id exists and names match then returns entity
        return entity;
    }
}
