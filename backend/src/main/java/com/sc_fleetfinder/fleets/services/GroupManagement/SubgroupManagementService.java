package com.sc_fleetfinder.fleets.services.GroupManagement;


import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementSubgroup;

import java.util.HashMap;
import java.util.List;

public interface SubgroupManagementService {

    GroupManagementSubgroup findSubgroupById(Long id);

    HashMap<Long, List<GroupManagementSubgroup>> findTreeMapByRootId(List<Long> rootIds, Long groupId);

    List<Long> findGroupCompositionRootIds(Long groupId);

    List<GroupManagementSubgroup> findSubgroupsByGroupId(Long groupId);

    GroupManagementSubgroup saveSubgroup(GroupManagementSubgroup subgroup);

    List<GroupManagementSubgroup> saveListOf(List<GroupManagementSubgroup> subgroups);

    void deleteSubgroup(GroupManagementSubgroup subgroup);

    GroupManagementSubgroup updateSubgroupLabel(GroupManagementSubgroup subgroup, String newLabel);
}
