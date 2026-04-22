package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberManagementService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberUserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/group-membership")
@PreAuthorize("isAuthenticated() and hasRole('user')")
@RequiredArgsConstructor
public class GroupMembershipController {

    private final UserService userService;
    private final GroupMemberUserServiceImpl memberUserService;

    @GetMapping("/my_groups")
    public Page<GroupMembershipResponseDto> getMyGroupMemberships(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return memberUserService.getMyGroupMemberships(user);
    }

    @GetMapping("/my_invite_authorized_groups")
    public Page<GroupListingResponseDto> getMyInviteAuthorizedGroupMemberships(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return memberUserService.getMyInviteAuthorizedMemberships(user);
    }

    @PostMapping("/request_invite")
    public ResponseEntity<?> requestGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                                @RequestBody SendGroupInviteRequestDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberUserService.sendGroupInviteRequest(
                user, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/accept_group_invite_offer")
    public ResponseEntity<?> acceptGroupInviteOffer(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users newMember = userService.verifyUser(kcId);

        GroupMembershipResponseDto responseDto = memberUserService.acceptGroupInviteOffer(newMember, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/decline_group_invite_offer")
    public ResponseEntity<?> declineGroupInviteOffer(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberUserService.declineGroupInviteOfferOrRequest(
                actingUser, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/rescind_group_invite_join_request")
    public ResponseEntity<?> rescindGroupInviteJoinRequest(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberUserService.rescindGroupInviteOfferOrRequest(
                actingUser, dto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/user_leave_group/{groupId}")
    public ResponseEntity<?> userLeaveGroup(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long groupId) {

        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        memberUserService.userLeaveGroup(actingUser, groupId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
