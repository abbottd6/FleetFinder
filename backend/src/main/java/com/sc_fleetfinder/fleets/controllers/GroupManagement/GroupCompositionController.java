package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.UpdateSubgroupDropListOrientationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.CrewTemplateService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupCompositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/group-composition")
@PreAuthorize("isAuthenticated() and hasRole('user')")
@RequiredArgsConstructor
public class GroupCompositionController {

    private final UserService userService;
    private final CrewTemplateService crewTemplateService;
    private final GroupCompositionService groupCompService;

    @GetMapping("/my-crew-templates")
    public ResponseEntity<?> getMyCrewTemplateOptions(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        List<CrewTemplateSummaryDto> myTemplates = crewTemplateService.fetchTemplateSummariesForUser(user);

        return ResponseEntity.ok(myTemplates);
    }

    @GetMapping("/get-existing-group-structure/{groupId}")
    public ResponseEntity<?> getGroupStructure(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        GroupCompositionDto responseDto = groupCompService.getExistingGroupComposition(user, groupId);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update-group-composition-tree")
    public ResponseEntity<?> updateGroupCompositionState(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody GroupCompositionDto groupCompDto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        groupCompService.updateGroupCompositionState(manager, groupCompDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-subgroup-from-template/{groupId}")
    public ResponseEntity<?> createSubgroupFromTemplate(@AuthenticationPrincipal Jwt jwt,
                                                        @PathVariable Long groupId,
                                                        @RequestBody CrewTemplateSummaryDto fromDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);


        GroupCompositionDto response = groupCompService.createStructureFromTemplate(user, groupId, fromDto.getTemplateId());


        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-subgroup/{groupId}/{subgroupId}")
    public ResponseEntity<?> softDeleteSubgroup(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long groupId,
                                                @PathVariable Long subgroupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        groupCompService.softDeleteSubgroup(user, groupId, subgroupId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/assign-member-position")
    public ResponseEntity<?> assignMemberPosition(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody GroupCompositionCrewPositionDto positionDto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        Long groupId = groupCompService.assignMemberPosition(manager, positionDto);

        return ResponseEntity.ok(groupId);
    }

    @PatchMapping("/clear-member-position-assignment")
    public ResponseEntity<?> clearPositionAssignment(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody GroupCompositionCrewPositionDto positionDto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        Long groupId = groupCompService.clearMemberPositionAssignment(manager, positionDto);

        return ResponseEntity.ok(groupId);
    }

    @PatchMapping("/clear-assignment-by-member")
    public ResponseEntity<?> clearAssignmentByMember(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody GroupManagerMemberResponseDto dto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        GroupManagerMemberResponseDto response = groupCompService.clearPositionAssignmentByMember(manager, dto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-subgroup-orientation")
    public ResponseEntity<?> updateSubgroupDropListOrientation(@AuthenticationPrincipal Jwt jwt,
                                                               @RequestBody UpdateSubgroupDropListOrientationDto dto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        groupCompService.updateSubgroupDropListOrientation(manager, dto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-subgroup-label/{subgroupId}")
    public ResponseEntity<?> updateSubgroupLabel(@AuthenticationPrincipal Jwt jwt,
                                                 @PathVariable Long subgroupId,
                                                 @RequestBody String newLabel) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        groupCompService.updateSubgroupLabelNoReturn(manager, subgroupId, newLabel);

        return ResponseEntity.ok().build();
    }
}
