package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import org.modelmapper.ModelMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServerRegionServiceImpl implements ServerRegionService {

    private final ServerRegionRepository serverRegionRepository;
    private final ModelMapper modelMapper;

    public ServerRegionServiceImpl(ServerRegionRepository serverRegionRepository) {
        super();
        this.serverRegionRepository = serverRegionRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<ServerRegionDto> getAllServerRegions() {
        List<ServerRegion> serverRegions = serverRegionRepository.findAll();
        return serverRegions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ServerRegionDto getServerRegionById(int id) {
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
