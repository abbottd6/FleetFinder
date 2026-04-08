package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-membership")
@PreAuthorize("isAuthenticated() and hasRole('user')")
@RequiredArgsConstructor
public class GroupMembershipController {

    private final UserService userService;
    private final GroupMemberService memberService;

    @GetMapping("/my_groups")
    public Page<GroupMembershipResponseDto> getMyGroupMemberships(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return memberService.getMyGroupMemberships(user);
    }

    @PostMapping("/request_invite")
    public ResponseEntity<?> requestGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                                @RequestBody SendGroupInviteRequestDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberService.sendGroupInviteRequest(
                user, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/send_invite")
    public ResponseEntity<?> sendGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                             @RequestBody SendGroupInviteOfferDto dto) {
        String kcId = jwt.getSubject();

        Users sender = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberService.sendGroupInviteOffer(sender, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/accept_group_invite_offer")
    public ResponseEntity<?> acceptGroupInviteOffer(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users newMember = userService.verifyUser(kcId);

        GroupMembershipResponseDto responseDto = memberService.acceptGroupInviteOffer(newMember, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/accept_group_invite_request")
    public ResponseEntity<?> acceptGroupInviteRequest(@AuthenticationPrincipal Jwt jwt,
                                                      @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        GroupManagerMemberResponseDto responseDto = memberService.acceptGroupInviteRequest(actingUser, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/decline_group_invite")
    public ResponseEntity<?> biDirectionalDeclineGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberService.declineGroupInviteOfferOrRequest(
                actingUser, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/rescind_group_invite_offer_or_request")
    public ResponseEntity<?> biDirectionalRescindGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody GroupInviteRequestOrResponseDto dto) {
        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        GroupInviteRequestOrResponseDto responseDto = memberService.rescindGroupInviteOfferOrRequest(
                actingUser, dto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/user_leave_group/{groupId}")
    public ResponseEntity<?> userLeaveGroup(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long groupId) {

        String kcId = jwt.getSubject();
        Users actingUser = userService.verifyUser(kcId);

        memberService.userLeaveGroup(actingUser, groupId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
