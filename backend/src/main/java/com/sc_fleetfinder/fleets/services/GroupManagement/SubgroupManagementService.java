package com.sc_fleetfinder.fleets.services.GroupManagement;


import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementSubgroup;

import java.util.HashMap;
import java.util.List;

public interface SubgroupManagementService {

    GroupManagementSubgroup findSubgroupById(Long id);

    HashMap<Long, List<GroupManagementSubgroup>> findTreeMapByRootId(List<Long> rootIds, Long groupId);

    List<Long> findGroupCompositionRootIds(Long groupId);

    GroupManagementSubgroup saveSubgroup(GroupManagementSubgroup subgroup);

    void deleteSubgroup(GroupManagementSubgroup subgroup);
}
