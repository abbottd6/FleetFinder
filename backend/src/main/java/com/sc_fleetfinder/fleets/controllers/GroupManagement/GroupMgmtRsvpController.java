package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/group-rsvp")
@PreAuthorize("isAuthenticated and hasRole('user')")
@RequiredArgsConstructor
public class GroupMgmtRsvpController {

    private final UserService userService;


}
