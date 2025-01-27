package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
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
public class PvpStatusServiceImpl implements PvpStatusService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final PvpStatusRepository pvpStatusRepository;
    private final ModelMapper modelMapper;

    public PvpStatusServiceImpl(PvpStatusRepository pvpStatusRepository) {
        super();
        this.pvpStatusRepository = pvpStatusRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "pvpStatusesCache", key = "'allPvpStatuses'")
    public List<PvpStatusDto> getAllPvpStatuses() {
        log.info("Caching test: getting all pvp statuses");
        List<PvpStatus> pvpStatuses = pvpStatusRepository.findAll();
        return pvpStatuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PvpStatusDto getPvpStatusById(Integer id) {
        Optional<PvpStatus> optionalPvpStatus = pvpStatusRepository.findById(id);
        if (optionalPvpStatus.isPresent()) {
            return convertToDto(optionalPvpStatus.get());
        }
        else {
            throw new ResourceNotFoundException("Pvp status with id " + id + " not found");
        }
    }

    public PvpStatusDto convertToDto(PvpStatus entity) {
        return modelMapper.map(entity, PvpStatusDto.class);
    }

    public PvpStatus convertToEntity(PvpStatusDto dto) {
        return modelMapper.map(dto, PvpStatus.class);
    }
}
