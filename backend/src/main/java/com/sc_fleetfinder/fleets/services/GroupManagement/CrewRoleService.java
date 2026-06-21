package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;

import java.util.List;

public interface CrewRoleService {

    List<CrewRoleClassification> getRolesForListingByUserId(Long userId);

    CrewRoleClassification findByRoleId(Long roleId);
}
