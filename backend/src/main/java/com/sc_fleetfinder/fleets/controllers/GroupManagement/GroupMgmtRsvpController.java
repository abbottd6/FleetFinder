package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.WrapperDtoRsvpActiveMastersResponse;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.RsvpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/group-rsvp")
@PreAuthorize("isAuthenticated and hasRole('user')")
@RequiredArgsConstructor
public class GroupMgmtRsvpController {

    private final UserService userService;
    private final RsvpService rsvpService;

    @GetMapping("/masters-list/{listingId}")
    public ResponseEntity<WrapperDtoRsvpActiveMastersResponse> getRsvpActiveMastersList(@AuthenticationPrincipal Jwt jwt,
                                                                                        @PathVariable Long listingId) {
        String kcId = jwt.getSubject();
        Users manager = userService.verifyUser(kcId);

        WrapperDtoRsvpActiveMastersResponse responseDto = rsvpService.getRsvpActiveMastersList(manager, listingId);

        return ResponseEntity.ok(responseDto);
    }

    // todo: create rsvp
    // start with just root level
    //implement frontend and backend checks to see if the subgroup has a nested child
    // with a scheduled rsvp, or if an ancestor has an rsvp

}

