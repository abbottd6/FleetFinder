package com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;

public interface ModListingActionConversionService {

    ModListingActionDto convertToDto(ModListingAction entity);
}
