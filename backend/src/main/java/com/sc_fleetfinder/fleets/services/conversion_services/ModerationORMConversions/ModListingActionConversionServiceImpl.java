package com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ModListingActionConversionServiceImpl implements ModListingActionConversionService {

    private final ModelMapper modListingActionToDtoMapper;

    ModListingActionConversionServiceImpl(ModelMapper modListingActionToDtoMapper) {
        this.modListingActionToDtoMapper = modListingActionToDtoMapper;
    }

    @Override
    public ModListingActionDto convertToDto(ModListingAction entity) {
        return modListingActionToDtoMapper.map(entity, ModListingActionDto.class);
    }
}
