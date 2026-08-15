package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionCrewPositionDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface PositionManagementService {

    List<CrewPosition> findAllPositionsForSubgroupTrees(List<Long> rootIds, Long groupId);

    Optional<CrewPosition> findMemberPositionByAssignedMemberIdAndLListingId(GroupMember member, Long groupId);

    List<CrewPosition> findAllPositionsByGroupId_IncludeDeleted(Long groupId);

    Optional<CrewPosition> findById(Long id);

    void softDeletePosition(Long positionId);

    void softDeleteAllBySubgroup(Long subgroupId);

    void restoreOrSkipSoftDeletedPositionMemberAssignment(GroupCompositionCrewPositionDto positionDto, CrewPosition entity);

    CrewPosition saveAndFlush(CrewPosition position);

    List<CrewPosition> saveListOf(List<CrewPosition> positions);
}
