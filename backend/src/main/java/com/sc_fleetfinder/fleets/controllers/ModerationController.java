package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.mod_services.ModerationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public CollectionModel<EntityModel<GroupListingResponseDto>> modGetAllGroupListings() {
        List<GroupListingResponseDto> groupListingResponseDto = mods.modGetAllGroupListings();

        List<EntityModel<GroupListingResponseDto>> groupListingModels = groupListingResponseDto.stream()
                .map(groupListing -> EntityModel.of(groupListing,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GroupListingsController.class)
                                .getAllGroupListings()).withSelfRel()))
                .toList();
        return CollectionModel.of(groupListingModels);
    }

    @DeleteMapping("/mod_delete_listing/{groupId}")
    @PreAuthorize("isAuthenticated() and hasRole('mod')")
    public ResponseEntity<?> modDeleteListing(@Valid @PathVariable Long groupId,
                                              @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        Users requestingMod = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User with Keycloak ID: " + keycloakId + " not found."));

        DeleteGroupListingDto modDeleteDto = new DeleteGroupListingDto(requestingMod.getUserId(), groupId);

        return mods.modDeleteListing(modDeleteDto);
    }
}
