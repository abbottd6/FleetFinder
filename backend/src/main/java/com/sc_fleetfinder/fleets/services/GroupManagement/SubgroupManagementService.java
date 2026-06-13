package com.sc_fleetfinder.fleets.services.GroupManagement;


import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementSubgroup;
import com.sc_fleetfinder.fleets.entities.Users;

import java.util.HashMap;
import java.util.List;

public interface SubgroupManagementService {

    GroupManagementSubgroup findSubgroupById(Long id);

    HashMap<Long, List<GroupManagementSubgroup>> findTreeMapByRootId(List<Long> rootIds, Long groupId);

    List<Long> findGroupCompositionRootIds(Long groupId);

    GroupManagementSubgroup saveSubgroup(GroupManagementSubgroup subgroup);

    void deleteSubgroup(GroupManagementSubgroup subgroup);

    GroupManagementSubgroup updateSubgroupLabel(GroupManagementSubgroup subgroup, String newLabel);
}
