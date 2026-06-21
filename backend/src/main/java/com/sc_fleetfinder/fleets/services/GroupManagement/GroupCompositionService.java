package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.UpdateSubgroupDropListOrientationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.FlattenedGroupCompDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionCrewPositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;

public interface GroupCompositionService {

    GroupCompositionDto getExistingGroupComposition(Users manager, Long groupId);

    GroupCompositionDto createStructureFromTemplate(Users manager, Long groupId, Long templateId);

    void updateGroupCompositionState(Users manager, GroupCompositionDto groupCompDto);

    FlattenedGroupCompDto flattenGroupCompositionDtoSubgroupsAndPositions(GroupCompositionDto groupCompDto);

    void softDeleteSubgroup(Users manager, Long groupId, Long subgroupId);

    void softDeletePosition(Users manager, Long groupId, Long positionId);

    void updateSubgroupDropListOrientation(Users manager, UpdateSubgroupDropListOrientationDto dto);

    GroupCompositionCrewPositionDto createNewPosition(Users manager, GroupCompositionCrewPositionDto positionDto);

    Long assignMemberPosition(Users manager, GroupCompositionCrewPositionDto dto);

    Long clearMemberPositionAssignment(Users manager, GroupCompositionCrewPositionDto dto);

    GroupManagerMemberResponseDto clearPositionAssignmentByMember(Users manager, GroupManagerMemberResponseDto dto);

    void updateSubgroupLabelNoReturn(Users manager, Long subgroupId, String newLabel);
}
