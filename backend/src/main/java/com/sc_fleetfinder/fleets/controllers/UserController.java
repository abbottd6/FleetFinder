package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.AddBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteBookmarkRequestDto;
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
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    // needs to use authentication principal and keycloakId instead of userId
    public PrivateUserResponseDto updateUser(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserDto updateUserDto) {
        String kcId = jwt.getSubject();

        return userService.updateUser(kcId, updateUserDto);
    }

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

    @PostMapping("/my/bookmarks")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> addBookmark(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody AddBookmarkRequestDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return bms.addBookmark(dto, user);
    }

    @DeleteMapping("/my/bookmarks/{groupId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long groupId) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        DeleteBookmarkRequestDto dto = new DeleteBookmarkRequestDto();
        dto.setUser(user);
        dto.setGroupId(groupId);

        return bms.deleteBookmarkById(dto);
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
