package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.AddBookmarkRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.AddHiddenRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.SubmitListingReportDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.HiddenListingService;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingBookmarkService;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingTemplateService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.reporting_services.ListingReportingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final HiddenListingService hls;
    private final ListingReportingService lrs;
    private final ListingBookmarkService bms;
    private final UserService userService;
    private final ListingTemplateService lts;

    public UserController(HiddenListingService hls,
                          ListingReportingService lrs,
                          ListingBookmarkService bms,
                          UserService userService,
                          ListingTemplateService lts) {
        this.hls = hls;
        this.lrs = lrs;
        this.bms = bms;
        this.userService = userService;
        this.lts = lts;
    };

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

        String discId = jwt.getClaimAsString("discord_user_id");
        String discName = jwt.getClaimAsString("discord_username");

        return userService.getUserByKeycloakIdAndCheckDiscord(kcId, discId, discName);
    }

    @PostMapping("/create-user")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public PrivateUserResponseDto createUser(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String discordId = jwt.getClaimAsString("discord_user_id");
        String discordUsername = jwt.getClaimAsString("discord_username");

        return userService.createUser(kcId, username, email, discordId, discordUsername);
    }

    @PutMapping("/update_me")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserDto updateUserDto) {
        String kcId = jwt.getSubject();

        userService.updateUser(kcId, updateUserDto);

        Map<String, String> response = new HashMap<>();
        response.put("response", "Profile updated.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/discord_me")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> manualDiscordLink(@AuthenticationPrincipal Jwt jwt) {
        String sessionState = jwt.getClaimAsString("sid");

        String keycloakDiscLinkUrl = userService.generateUserDiscordLink(sessionState);

        Map<String, String> response = new HashMap<>();
        response.put("url", keycloakDiscLinkUrl);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove_discord")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> removeDiscordLink(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();

        PrivateUserResponseDto responseDto = userService.removeDiscordAccountLink(kcId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete_me")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
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

    @PostMapping("/my/bookmarks_get")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getBookmarks(@AuthenticationPrincipal Jwt jwt,
                                          @RequestBody GenericPageRequestDto pageDto) {
        String kcId = jwt.getSubject();
        Long userId = userService.verifyUser(kcId).getUserId();

        return bms.getBookmarksByUserId(userId, pageDto);
    }

    @PostMapping("/my/bookmarks")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> addBookmark(@AuthenticationPrincipal Jwt jwt,
                                         @RequestBody AddBookmarkRequestDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        return bms.addBookmark(dto.getGroupId(), user);
    }

    //user's ownership is verified by searching by user + group in bookmark repo
    @DeleteMapping("/my/bookmarks/{groupId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long groupId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        return bms.deleteBookmarkById(groupId, user);
    }

    @DeleteMapping("/my/bookmarks/delete_multiple")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteMultipleBookmarks(@AuthenticationPrincipal Jwt jwt, @RequestBody Set<Long> groupIds) {

        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return bms.deleteMultipleBookmarks(user, groupIds);
    }

    @PostMapping("/my/templates/get")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public Page<ListingTemplateResponseDto> getTemplates(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody SortablePageRequestDto pageDto) {

        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        Sort sort = Sort.unsorted();

        if(pageDto.getSortField() != null && !pageDto.getSortField().isBlank()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(pageDto.getSortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, pageDto.getSortField());
        }

        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sort);

        return lts.getMyTemplates(user, pageable);
    }

    @PostMapping("/my/templates/save")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> createTemplate(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody CreateGroupListingDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return lts.createTemplate(user, dto);
    }

    @DeleteMapping("/my/templates/delete/{templateId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteTemplate(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long templateId) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);

        return lts.removeTemplate(user, templateId);
    }

    @PostMapping("/my/hidden:add")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> addHidden(@AuthenticationPrincipal Jwt jwt,
                                       @RequestBody AddHiddenRequestDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        return hls.userAddHidden(user, dto.getGroupId());
    }

    @DeleteMapping("/my/hidden:clear")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> clearHidden(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        return hls.userClearHidden(user);
    }

    @DeleteMapping("/my/hidden:pop")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> undoLastHide(@AuthenticationPrincipal Jwt jwt) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        return hls.userUndoLastHide(user);
    }

    @PostMapping("/group_listings/submit_report")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> submitListingReport(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestBody SubmitListingReportDto dto) {
        String kcId = jwt.getSubject();

        Users user = userService.verifyUser(kcId);

        return lrs.generateListingReport(dto, user);
    }
}
