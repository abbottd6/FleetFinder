package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.CrewTemplateService;
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

        //TODO
        // verify user group authorization
        // find template by id
        // find template subgroups by id
        // find template positions by subgroup ids
        // map structure onto a subgroup entity for this group
        // figure out how to map this to a json and return it

        return ResponseEntity.ok().build();
    }
}
