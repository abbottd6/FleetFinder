package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;

public interface PvpStatusConversionService {

    PvpStatusDto convertToDto(PvpStatus pvpStatus);
    PvpStatus convertToEntity(PvpStatusDto pvpStatusDto);
}
