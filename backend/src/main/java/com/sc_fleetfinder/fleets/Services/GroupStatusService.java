package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.GroupStatus;

import java.util.List;

public interface GroupStatusService {

    List<GroupStatus> getAllGroupStatuses();
    GroupStatus getGroupStatusById(int id);
}
