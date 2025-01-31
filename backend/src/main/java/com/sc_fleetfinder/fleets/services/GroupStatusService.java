package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;

import java.util.List;

public interface GroupStatusService {

    List<GroupStatusDto> getAllGroupStatuses();
    GroupStatusDto getGroupStatusById(Integer id);
    GroupStatusDto convertToDto(GroupStatus entity);
}
