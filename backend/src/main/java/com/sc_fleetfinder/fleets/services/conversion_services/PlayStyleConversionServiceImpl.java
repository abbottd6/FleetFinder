package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PlayStyleConversionServiceImpl implements PlayStyleConversionService {

    private static final Logger log = LoggerFactory.getLogger(PlayStyleConversionServiceImpl.class);
    private final PlayStyleRepository playStyleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayStyleConversionServiceImpl(PlayStyleRepository playStyleRepository) {
        this.playStyleRepository = playStyleRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public PlayStyleDto convertToDto(PlayStyle entity) {
        //id valid check
        if(entity.getStyleId() == null || entity.getStyleId() == 0) {
            log.error("Encountered null or zero play style Id when converting from entity to Dto");
            throw new ResourceNotFoundException("Encountered null or zero legality Id");
        }

        //type/name valid check
        if(entity.getPlayStyle() == null || entity.getPlayStyle().isEmpty()) {
            log.error("Encountered null or empty play style name when converting from entity to Dto");
            throw new ResourceNotFoundException("Encountered null or empty status");
        }

        return modelMapper.map(entity, PlayStyleDto.class);
    }

    @Override
    public PlayStyle convertToEntity(PlayStyleDto dto) {
        //checking repository for entity matching dto Id
        PlayStyle entity = playStyleRepository.findById(dto.getStyleId())
                .orElseThrow(() -> {
                    log.error("Encountered play style with unmatched Id: {}, when converting from dto to entity",
                            dto.getStyleId());
                    return new ResourceNotFoundException("Play style with Id: {} not found", dto.getStyleId());
                });

        //verifying name/id match for dto  and entity
        if(!Objects.equals(dto.getPlayStyle(), entity.getPlayStyle())) {
            log.error("Encountered play style with style name and Id mismatch: {}, when converting from Dto to " +
                    "entity", dto.getPlayStyle());
            throw new ResourceNotFoundException("Play style mismatch for Dto: " + dto.getPlayStyle() + ", ID: " +
                    dto.getStyleId() + " and entity: " + entity.getPlayStyle() + ", ID: " + entity.getStyleId());
        }

        //if Id exists and names match, then returns entity
        return entity;
    }
}
