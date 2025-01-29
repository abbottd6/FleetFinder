package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
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

        if(pvpStatuses.isEmpty()) {
            throw new ResourceNotFoundException("pvpStatuses");
        }

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
            throw new ResourceNotFoundException(id);
        }
    }

    public PvpStatusDto convertToDto(PvpStatus entity) {
        return modelMapper.map(entity, PvpStatusDto.class);
    }

    public PvpStatus convertToEntity(PvpStatusDto dto) {
        return modelMapper.map(dto, PvpStatus.class);
    }
}
