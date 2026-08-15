package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;

import java.util.List;
import java.util.Map;

public interface CrewRoleService {

    List<CrewRoleClassification> getRolesForListingByUserId(Long userId);

    CrewRoleClassification findByRoleId(Long roleId);

    Map<Long, CrewRoleClassification> constructCrewRolesMapForGroup(Long groupId);
}
