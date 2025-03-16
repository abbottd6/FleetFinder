package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;

public interface GroupStatusConversionService {

    GroupStatusDto convertToDto(GroupStatus groupStatus);
    GroupStatus convertToEntity(GroupStatusDto groupStatusDto);
}
