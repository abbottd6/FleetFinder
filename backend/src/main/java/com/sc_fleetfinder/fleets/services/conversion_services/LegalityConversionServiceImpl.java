package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LegalityConversionServiceImpl implements LegalityConversionService {

    private static final Logger log = LoggerFactory.getLogger(LegalityConversionServiceImpl.class);
    private final LegalityRepository legalityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LegalityConversionServiceImpl(LegalityRepository legalityRepository) {
        this.legalityRepository = legalityRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public LegalityDto convertToDto(Legality entity) {
        //id valid check
        if(entity.getLegalityId() == null || entity.getLegalityId() == 0) {
            throw new ResourceNotFoundException("Legality Id is null or 0");
        }
        //type/name valid check
        if(entity.getLegalityStatus() == null || entity.getLegalityStatus().isEmpty()) {
            throw new ResourceNotFoundException("Legality status is null or empty");
        }

        return modelMapper.map(entity, LegalityDto.class);
    }

    @Override
    public Legality convertToEntity(LegalityDto dto) {
        //checking repository for entity matching Dto id
        Legality entity = legalityRepository.findById(dto.getLegalityId())
                .orElseThrow(() -> new ResourceNotFoundException("Legality with ID: " + dto.getLegalityId() +
                        " not found"));
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getLegalityStatus(), entity.getLegalityStatus())) {
            throw new ResourceNotFoundException("Legality status mismatch for Dto: " + dto.getLegalityStatus() +
                    ", ID: " + dto.getLegalityId() + " and entity: " + entity.getLegalityStatus() + ", ID: " +
                    entity.getLegalityId());
        }
        //if Id exists and names match, then returns entity
        return entity;
    }
}
