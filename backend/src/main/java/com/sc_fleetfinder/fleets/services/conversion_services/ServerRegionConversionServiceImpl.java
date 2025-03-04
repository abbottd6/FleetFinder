package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ServerRegionConversionServiceImpl implements ServerRegionConversionService {

    private static final Logger log = LoggerFactory.getLogger(ServerRegionConversionServiceImpl.class);
    private final ServerRegionRepository serverRegionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ServerRegionConversionServiceImpl(ServerRegionRepository serverRegionRepository) {
        this.serverRegionRepository = serverRegionRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ServerRegionDto convertToDto(ServerRegion entity) {
        //id valid check
        if(entity.getServerId() == null || entity.getServerId() == 0) {
            log.error("Encountered null or zero server Id when converting from entity to Dto.");
            throw new IllegalArgumentException("Encountered null or zero server Id when converting from " +
                    "entity to Dto.");
        }

        //type/name valid check
        if(entity.getServerName() == null || entity.getServerName().isEmpty()) {
            log.error("Encountered null or empty server Name when converting from entity to Dto.");
            throw new IllegalArgumentException("Encountered null or empty server Name when converting from " +
                    "entity to Dto.");
        }

        //else
        return modelMapper.map(entity, ServerRegionDto.class);
    }

    @Override
    public ServerRegion convertToEntity(ServerRegionDto dto) {
        //checking repository for entity matching Dto id
        ServerRegion entity = serverRegionRepository.findById(dto.getServerId())
                .orElseGet(() -> {
                    log.error("Encountered server with unmatched Id: {}, when converting from dto to entity",
                            dto.getServerId());
                    throw new ResourceNotFoundException("Encountered server with unmatched Id: " +
                            dto.getServerId() + " when converting from dto to entity");
                });

        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getServername(), entity.getServerName())) {
            log.error("Encountered server with status name and Id mismatch when converting from dto to " +
                    "entity. Id: {} - server name: {}", dto.getServerId(), dto.getServername());
            throw new IllegalArgumentException("Server name/id mismatch for Dto with Id: " +
                    dto.getServerId() + " - server name: " + dto.getServername());
        }

        //else-if id exists and names match, then return entity
        return entity;
    }
}
