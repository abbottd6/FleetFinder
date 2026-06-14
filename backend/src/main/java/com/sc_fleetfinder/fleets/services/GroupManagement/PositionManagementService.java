package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;

import java.util.List;
import java.util.Optional;

public interface PositionManagementService {

    List<CrewPosition> findAllPositionsForSubgroupTrees(List<Long> rootIds, Long groupId);

    Optional<CrewPosition> findMemberPositionByAssignedMemberIdAndLListingId(GroupMember member, Long groupId);

    List<CrewPosition> findAllPositionsByGroupId(Long groupId);

    Optional<CrewPosition> findById(Long id);

    CrewPosition saveAndFlush(CrewPosition position);

    List<CrewPosition> saveListOf(List<CrewPosition> positions);
}
