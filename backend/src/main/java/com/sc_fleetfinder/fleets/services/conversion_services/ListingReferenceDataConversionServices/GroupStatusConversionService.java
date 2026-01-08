package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;

public interface GroupStatusConversionService {

    GroupStatusDto convertToDto(GroupStatus groupStatus);
    GroupStatus convertToEntity(GroupStatusDto groupStatusDto);
}
