package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerInviteResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/group-member-management")
@PreAuthorize("isAuthenticated() and hasRole('user')")
@RequiredArgsConstructor
public class GroupMemberManagementController {

    private final UserService userService;
    private final GroupMemberManagementService memberManagementService;

    @GetMapping("/verify_group_management_authz/{groupId}")
    public ResponseEntity<?> verifyGroupManagementAuth(@AuthenticationPrincipal Jwt jwt,
                                                       @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Boolean isAuthorized = memberManagementService.verifyUserIsAuthorizedMember(user, groupId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthorized", isAuthorized);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_active_roster/{groupId}")
    public ResponseEntity<?> getActiveRosterGroupMembers(@AuthenticationPrincipal Jwt jwt,
                                                         @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Page<GroupManagerMemberResponseDto> activeRoster = memberManagementService.getActiveRosterGroupMembers(user, groupId);

        return ResponseEntity.ok(activeRoster);
    }

    @GetMapping("/get_waitlist_members/{groupId}")
    public ResponseEntity<?> getWaitlistMembers(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Page<GroupManagerMemberResponseDto> waitlistRoster = memberManagementService.getWaitlistMembers(user, groupId);

        return ResponseEntity.ok(waitlistRoster);
    }

    @GetMapping("/get_group_invites/{groupId}")
    public ResponseEntity<?> getGroupInvites(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Page<GroupManagerInviteResponseDto> invitesPage = memberManagementService.getGroupInvitesPage(user, groupId);

        return ResponseEntity.ok(invitesPage);
    }

    @PostMapping("/accept_group_invite_request")
    public ResponseEntity<?> acceptGroupInviteRequest(@AuthenticationPrincipal Jwt jwt,
                                                      @RequestBody GroupManagerInviteResponseDto dto) {
        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        GroupManagerMemberResponseDto responseDto = memberManagementService.acceptGroupInviteRequest(actingUser, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/send_invite")
    public ResponseEntity<?> sendGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                             @RequestBody SendGroupInviteOfferDto dto) {
        String kcId = jwt.getSubject();

        Users sender = userService.verifyUser(kcId);

        GroupManagerInviteResponseDto responseDto = memberManagementService.sendGroupInviteOffer(sender, dto);

        return ResponseEntity.ok(responseDto);
    }
}
