package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;

import java.util.List;

public interface GroupStatusService {

    List<GroupStatusDto> getAllGroupStatuses();
    GroupStatusDto getGroupStatusById(int id);
}
