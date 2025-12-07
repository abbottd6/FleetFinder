package com.sc_fleetfinder.fleets.controllers;


import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SearchListingsDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Slf4j
public class GroupListingsController {

    @Autowired
    private GroupListingService groupListingService;
    @Autowired
    private UserRepository userRepository;

    //##TODO make sure that all secure endpoints derive identity from the token and that the authorized user for...
    //##TODO any requests to access or modify a resource match the resource owner

    @PostMapping("/search")
    public Page<GroupListingResponseDto> searchGroupListings(@RequestBody SearchListingsDto request) {
        Sort sort = Sort.unsorted();
        if (request.getSortField() != null && !request.getSortField().isBlank()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getSortField());
        }
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

//        log.info("Here is the page:" + request);

        return groupListingService.searchGroupListings(request, pageable);
    }

    //TODO this is probably not necessary anymore since adding the search listings endpoint
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
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> createGroupListing(@Valid @RequestBody CreateGroupListingDto dto,
                                                @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        Users requestingUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. Try logging out and logging back in."));

        dto.setUserId(requestingUser.getUserId());

        return groupListingService.createGroupListing(dto, requestingUser);
    }

    @PutMapping("/update_listing")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> updateGroupListing(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateGroupListingDto dto) {
        String keycloakId = jwt.getSubject();

        Users requestingUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User with Keycloak ID: " + keycloakId + " not found"));

        return groupListingService.updateGroupListing(dto, requestingUser);
    }

    @DeleteMapping("/delete_listing/{groupId}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteGroupListing(@Valid @PathVariable Long groupId,
                                                   @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        Users requestingUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User with Keycloak ID: " + keycloakId + " not found"));

        return groupListingService.deleteGroupListing(groupId, requestingUser);
    }
}
