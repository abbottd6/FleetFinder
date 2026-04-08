package com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ModListingActionConversionServiceImpl implements ModListingActionConversionService {

    private final ModelMapper modelMapper;

    ModListingActionConversionServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ModListingActionDto convertToDto(ModListingAction entity) {
        return modelMapper.map(entity, ModListingActionDto.class);
    }
}
