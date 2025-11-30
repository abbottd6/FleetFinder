package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GroupStatusDto;

import java.util.List;

public interface GroupStatusService {

    List<GroupStatusDto> getAllGroupStatuses();
    GroupStatusDto getGroupStatusById(Integer id);
}
