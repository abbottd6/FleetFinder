package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.entities.Users;

public interface GroupCompositionService {

    GroupCompositionDto createStructureFromTemplate(Users user, Long groupId, Long templateId);
}
