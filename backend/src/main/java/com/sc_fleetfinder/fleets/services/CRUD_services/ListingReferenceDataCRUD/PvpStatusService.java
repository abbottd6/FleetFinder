package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PvpStatusDto;

import java.util.List;

public interface PvpStatusService {

    List<PvpStatusDto> getAllPvpStatuses();
    PvpStatusDto getPvpStatusById(Integer id);
}
