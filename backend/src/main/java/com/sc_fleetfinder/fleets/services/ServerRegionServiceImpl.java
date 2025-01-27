package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
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
public class ServerRegionServiceImpl implements ServerRegionService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final ServerRegionRepository serverRegionRepository;
    private final ModelMapper modelMapper;

    public ServerRegionServiceImpl(ServerRegionRepository serverRegionRepository) {
        super();
        this.serverRegionRepository = serverRegionRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "serverRegionsCache", key = "'allServerRegionsCache'")
    public List<ServerRegionDto> getAllServerRegions() {
        log.info("Caching test: getting all server regions");
        List<ServerRegion> serverRegions = serverRegionRepository.findAll();
        return serverRegions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ServerRegionDto getServerRegionById(Integer id) {
        Optional<ServerRegion> optionalServerRegion = serverRegionRepository.findById(id);
        if(optionalServerRegion.isPresent()) {
            return convertToDto(optionalServerRegion.get());
        }
        else {
            throw new ResourceNotFoundException("Server region with id " + id + " not found");
        }
    }

    public ServerRegionDto convertToDto(ServerRegion entity) {
        return modelMapper.map(entity, ServerRegionDto.class);
    }

    public ServerRegion convertToEntity(ServerRegionDto dto) {
        return modelMapper.map(dto, ServerRegion.class);
    }
}
