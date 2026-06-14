package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.UpdateSubgroupDropListOrientationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.FlattenedGroupCompDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionCrewPositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;

public interface GroupCompositionService {

    GroupCompositionDto getExistingGroupComposition(Users user, Long groupId);

    GroupCompositionDto createStructureFromTemplate(Users user, Long groupId, Long templateId);

    void updateGroupCompositionState(Users manager, GroupCompositionDto groupCompDto);

    FlattenedGroupCompDto flattenGroupCompositionDtoSubgroupsAndPositions(GroupCompositionDto groupCompDto);

    void deleteSubgroup(Users user, Long groupId, Long subgroupId);

    void updateSubgroupDropListOrientation(Users user, UpdateSubgroupDropListOrientationDto dto);

    Long assignMemberPosition(Users manager, GroupCompositionCrewPositionDto dto);

    Long clearMemberPositionAssignment(Users manager, GroupCompositionCrewPositionDto dto);

    GroupManagerMemberResponseDto clearPositionAssignmentByMember(Users manager, GroupManagerMemberResponseDto dto);

    void updateSubgroupLabelNoReturn(Users manager, Long subgroupId, String newLabel);
}
