package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.AddNewSubgroupRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.CreateOrEditCrewPositionDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.TemplateFromCompRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.UpdateSubgroupDropListOrientationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.CrewTemplateService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupCompositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/get-available-role-classifications/{groupId}")
    public ResponseEntity<?> getAvailableRoleClass(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        List<GroupRoleSummaryDto> availableRoleClassifications = groupCompService.getAvailableRoleClassifications(manager, groupId);

        return ResponseEntity.ok(availableRoleClassifications);
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

    @PostMapping("/create-template-from-subgroup")
    public ResponseEntity<?> createTemplateFromSubgroup(@AuthenticationPrincipal Jwt jwt,
                                                        @RequestBody @Valid TemplateFromCompRequestDto templateFromDto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        String savedLabel = crewTemplateService.createTemplateFromCompositionDto(manager, templateFromDto);

        Map<String, String> response = new HashMap<>();
        response.put("savedLabel", savedLabel);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-subgroup")
    public ResponseEntity<?> addSubgroup(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody @Validated AddNewSubgroupRequestDto requestDto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        GroupCompositionSubgroupDto response = groupCompService.addSubgroup(manager, requestDto);

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

    @DeleteMapping("/delete-position/{groupId}/{positionId}")
    public ResponseEntity<?> softDeletePosition(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long groupId,
                                                @PathVariable Long positionId) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        groupCompService.softDeletePosition(manager, groupId, positionId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-new-position")
    public ResponseEntity<?> createNewPosition(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody @Validated CreateOrEditCrewPositionDto positionDto) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        GroupCompositionCrewPositionDto response = groupCompService.createNewPosition(manager, positionDto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit-crew-position/{positionId}")
    public ResponseEntity<?> editCrewPosition(@AuthenticationPrincipal Jwt jwt,
                                              @RequestBody @Validated CreateOrEditCrewPositionDto positionDto,
                                              @PathVariable Long positionId) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        GroupCompositionCrewPositionDto response = groupCompService.editCrewPosition(manager, positionId, positionDto);

        return ResponseEntity.ok(response);
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
