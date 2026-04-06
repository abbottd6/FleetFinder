package com.sc_fleetfinder.fleets.controllers.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteOfferDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.SendGroupInviteRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public Page<GroupMembershipResponseDto> getMyMemberGroups(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return memberService.getMyGroupMemberships(user);
    }

    @PostMapping("/request_invite")
    public ResponseEntity<?> requestGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                                @RequestBody SendGroupInviteRequestDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        GroupInviteResponseDto responseDto = memberService.sendGroupInviteRequest(
                user, dto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/send_invite")
    public ResponseEntity<?> sendGroupInvite(@AuthenticationPrincipal Jwt jwt,
                                             @RequestBody SendGroupInviteOfferDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);
    }
}
