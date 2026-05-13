package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.entities.Users;

public interface GroupCompositionService {

    GroupCompositionDto getExistingGroupComposition(Users user, Long groupId);

    GroupCompositionDto createStructureFromTemplate(Users user, Long groupId, Long templateId);

    void deleteSubgroup(Users user, Long groupId, Long subgroupId);
}
