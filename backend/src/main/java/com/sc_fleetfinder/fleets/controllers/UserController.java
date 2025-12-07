package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.SubmitListingReportDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingBookmarkService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.reporting_services.ListingReportingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ListingBookmarkService bms;

    @Autowired
    private ListingReportingService lrs;

    //##TODO make sure that all secure endpoints derive identity from the token and that the authorized user for...
    //##TODO any requests to access or modify a resource match the resource owner
    public UserController() {};

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public List<PublicUserResponseDto> getUsers() {
        return userService.getAllUsers();
    }

    //TODO This can be used once user profiles are viewable by others
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PublicUserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PrivateUserResponseDto getMe(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        return userService.getUserByKeycloakId(kcId);
    }

    @PostMapping("/create-user")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PrivateUserResponseDto createUser(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        return userService.createUser(kcId, username, email);
    }

    //TODO ensure that this is not using a userId passed from the frontend
    //TODO should only use the token derived id
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    // needs to use authentication principal and keycloakId instead of userId
    public PrivateUserResponseDto updateUser(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserDto updateUserDto) {
        String kcId = jwt.getSubject();

        return userService.updateUser(kcId, updateUserDto);
    }

    //TODO ensure that this is not using a userId passed from the frontend
    //TODO should only use the token derived id
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    // needs to use authentication principal and keycloakId instead of userId
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        userService.deleteUser(kcId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/bookmarks_brief")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getBookmarkBrief(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        Long userId = userService.verifyUser(kcId).getUserId();

        return bms.getBookmarkBriefByUserId(userId);
    }

    @GetMapping("/my/bookmarks")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getBookmarks(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        Long userId = userService.verifyUser(kcId).getUserId();

        return bms.getBookmarksByUserId(userId);
    }

    @PostMapping("/my/bookmarks/{groupId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> addBookmark(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable Long groupId) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return bms.addBookmark(groupId, user);
    }

    //TODO should remove userId from the dto and just pass it as a separate argument
    //TODO should also verify that the bookmark belongs to the authorized/requesting user
    @DeleteMapping("/my/bookmarks/{groupId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long groupId) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return bms.deleteBookmarkById(groupId, user);
    }

    @PostMapping("/group_listings/submit_report")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> submitListingReport(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestBody SubmitListingReportDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return lrs.generateListingReport(dto, user);
    }

    @GetMapping("/group_listings/report_brief")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getListingReportBrief(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return lrs.getUsersReportBrief(user);
    }
}
