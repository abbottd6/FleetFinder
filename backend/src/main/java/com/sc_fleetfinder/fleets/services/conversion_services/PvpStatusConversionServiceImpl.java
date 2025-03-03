package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PvpStatusConversionServiceImpl implements PvpStatusConversionService {

    private static final Logger log = LoggerFactory.getLogger(PvpStatusConversionServiceImpl.class);
    private final PvpStatusRepository pvpStatusRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PvpStatusConversionServiceImpl(PvpStatusRepository pvpStatusRepository) {
        this.pvpStatusRepository = pvpStatusRepository;
        this.modelMapper = new ModelMapper();
    }

    public PvpStatusDto convertToDto(PvpStatus entity) {
        //id valid check
        if(entity.getPvpStatusId() == null || entity.getPvpStatusId() == 0) {
            log.error("Encountered null or zero pvp status Id when converting from entity to Dto");
            throw new ResourceNotFoundException("Encountered null or zero legality Id");
        }

        //type/name valid check
        if(entity.getPvpStatus() == null || entity.getPvpStatus().isEmpty()) {
            log.error("Encountered null or empty pvp status name when converting from entity to Dto");
            throw new ResourceNotFoundException("Encountered null or empty pvp status name");
        }
        //else
        return modelMapper.map(entity, PvpStatusDto.class);
    }

    public PvpStatus convertToEntity(PvpStatusDto dto) {
        //check repository for entity matching dto id
        PvpStatus entity = pvpStatusRepository.findById(dto.getPvpStatusId())
                .orElseGet(() -> {
                    log.error("Encountered pvp status with unmatched Id: {}, when converting from dto to entity",
                            dto.getPvpStatusId());
                    throw new ResourceNotFoundException("Pvp status with Id: {} not found", dto.getPvpStatusId());
                });

        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getPvpStatus(), entity.getPvpStatus())) {
            log.error("Encountered pvp status with status name and Id mismatch when converting from Dto to entity. " +
                    "Dto Id: {}, and pvp status: {}, do not match an entity", dto.getPvpStatusId(), dto.getPvpStatus());
            throw new ResourceNotFoundException("PvpStatus name mismatch for Dto with Id: " +
                    dto.getPvpStatusId() + ", and pvp status: " + dto.getPvpStatus() + ".");
        }

        //if Id exists and names match, then return entity
        return entity;
    }
}
