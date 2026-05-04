package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementSubgroup;
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

    @PostMapping("/create-subgroup-from-template/{groupId}")
    public ResponseEntity<?> createSubgroupFromTemplate(@AuthenticationPrincipal Jwt jwt,
                                                        @PathVariable Long groupId,
                                                        @RequestBody CrewTemplateSummaryDto fromDto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);


        GroupCompositionDto response = groupCompService.createStructureFromTemplate(user, groupId, fromDto.getTemplateId());


        return ResponseEntity.ok(response);
    }
}
