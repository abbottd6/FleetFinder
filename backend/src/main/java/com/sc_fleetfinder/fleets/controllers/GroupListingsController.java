package com.sc_fleetfinder.fleets.controllers;


import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/group-listings")
public class GroupListingsController {

    @Autowired
    private GroupListingService groupListingService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public CollectionModel<EntityModel<GroupListingResponseDto>> getAllGroupListings() {
        List<GroupListingResponseDto> groupListingResponseDto = groupListingService.getAllGroupListings();

        List<EntityModel<GroupListingResponseDto>> groupListingModels = groupListingResponseDto.stream()
                .map(groupListing -> EntityModel.of(groupListing,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GroupListingsController.class).getAllGroupListings()).withSelfRel()))
                .toList();
        return CollectionModel.of(groupListingModels);
    }

    @GetMapping("/{id}")
    public GroupListingResponseDto getGroupListingById(@PathVariable Long id) {
        return groupListingService.getGroupListingById(id);
    }

    @PostMapping("/create_listing")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createGroupListing(@Valid @RequestBody CreateGroupListingDto createGroupListingDto,
                                                @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        Users requestingUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User with Keycloak ID: " + keycloakId + " not found"));

        createGroupListingDto.setUserId(requestingUser.getUserId());

        return groupListingService.createGroupListing(createGroupListingDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    // needs to use authenticationPrincipal and keycloakId instead of userId
    public GroupListing updateGroupListing(@PathVariable Long id, @Valid @RequestBody UpdateGroupListingDto updateGroupListingDto) {
        return groupListingService.updateGroupListing(id, updateGroupListingDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    // needs to use authenticationprincipal and keycloakId instead of userid
    public ResponseEntity<Void> deleteGroupListing(@PathVariable Long id) {
        groupListingService.deleteGroupListing(id);
        return ResponseEntity.noContent().build();
    }
}
