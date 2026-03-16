package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ModClearIssueDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SortablePageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ManualModDeleteDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.mod_services.ModerationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/modctrl")
@PreAuthorize("isAuthenticated() and hasRole('mod')")
@Slf4j
public class ModerationController {

    @Autowired
    private ModerationService mods;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/mod_delete_listing")
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public ResponseEntity<?> modDeleteListing(@Valid @RequestBody ManualModDeleteDto dto,
                                              @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        Users requestingMod = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User with Keycloak ID: " + keycloakId + " not found."));

        return mods.modDeleteListing(dto, requestingMod);
    }

    @PutMapping("/mod_clear_issue")
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public ResponseEntity<?> modClearIssue(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ModClearIssueDto dto) {
        String keycloakId = jwt.getSubject();

        Users requestingMod = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User with Keycloak ID: " + keycloakId + " not found."));

        return mods.modClearIssue(dto, requestingMod);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public Page<GroupListingResponseDto> modGetAllGroupListings(SortablePageRequestDto pageDto) {
        Sort sort = Sort.unsorted();
        if (pageDto.getSortField() != null && !pageDto.getSortField().isBlank()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(pageDto.getSortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, pageDto.getSortField());
        }

        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sort);

        return mods.modGetAllGroupListings(pageable);
    }

    @PostMapping("/get_issues_page")
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public Page<ModerationIssueResponseDto> modGetAllIssues(@RequestBody SortablePageRequestDto pageDto) {
        Sort sort = Sort.unsorted();
        if (pageDto.getSortField() != null && !pageDto.getSortField().isBlank()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(pageDto.getSortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, pageDto.getSortField());
        }

        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sort);

        return mods.modGetAllIssues(pageable);
    }

    @PostMapping("/weeks_actions")
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public Page<ModListingActionDto> getThisWeeksModActions(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody GenericPageRequestDto pageDto) {
        String keycloakId = jwt.getSubject();

        userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User with Keycloak ID: " + keycloakId + " not found."));

        Pageable pageable = PageRequest.of(pageDto.getPageIdx(), pageDto.getPageSize());

        return mods.getThisWeeksModListingActions(pageable);
    }
}
