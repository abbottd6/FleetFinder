package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.CreateOrEditCrewPositionDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.UpdateSubgroupDropListOrientationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.Users;

import java.util.List;

public interface GroupCompositionService {

    GroupCompositionDto getExistingGroupComposition(Users manager, Long groupId);

    List<GroupRoleSummaryDto> getAvailableRoleClassifications(Users manager, Long groupId);

    GroupCompositionDto createStructureFromTemplate(Users manager, Long groupId, Long templateId);

    void updateGroupCompositionState(Users manager, GroupCompositionDto groupCompDto);

    FlattenedGroupCompDto flattenGroupCompositionDtoSubgroupsAndPositions(GroupCompositionDto groupCompDto);

    void softDeleteSubgroup(Users manager, Long groupId, Long subgroupId);

    void softDeletePosition(Users manager, Long groupId, Long positionId);

    void updateSubgroupDropListOrientation(Users manager, UpdateSubgroupDropListOrientationDto dto);

    GroupCompositionCrewPositionDto createNewPosition(Users manager, CreateOrEditCrewPositionDto positionDto);

    GroupCompositionCrewPositionDto editCrewPosition(Users manager, Long positionId, CreateOrEditCrewPositionDto positionDto);

    Long assignMemberPosition(Users manager, GroupCompositionCrewPositionDto dto);

    Long clearMemberPositionAssignment(Users manager, GroupCompositionCrewPositionDto dto);

    GroupManagerMemberResponseDto clearPositionAssignmentByMember(Users manager, GroupManagerMemberResponseDto dto);

    void updateSubgroupLabelNoReturn(Users manager, Long subgroupId, String newLabel);
}
