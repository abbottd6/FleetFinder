package com.sc_fleetfinder.fleets.controllers;


import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
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

    @PutMapping("/update_listing")
    @PreAuthorize("isAuthenticated()")
    // needs to use authenticationPrincipal and keycloakId instead of userId
    public GroupListing updateGroupListing(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateGroupListingDto updateGroupListingDto) {
        String keycloakId = jwt.getSubject();

        Users requestingUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User with Keycloak ID: " + keycloakId + " not found"));

        updateGroupListingDto.setUserId(requestingUser.getUserId());
        updateGroupListingDto.setGroupId(updateGroupListingDto.getGroupId());

        return groupListingService.updateGroupListing(updateGroupListingDto);
    }

    @DeleteMapping("/delete_listing/{id}")
    @PreAuthorize("isAuthenticated()")
    // id is the listingId
    public ResponseEntity<?> deleteGroupListing(@Valid @PathVariable Long id,
                                                   @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        Users requestingUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User with Keycloak ID: " + keycloakId + " not found"));

        DeleteGroupListingDto deleteDto = new DeleteGroupListingDto(requestingUser.getUserId(), id);

        log.info("Service bean class: {}", groupListingService.getClass().getName());
        log.info("AOP proxy? {}", AopUtils.isAopProxy(groupListingService));

        return groupListingService.deleteGroupListing(deleteDto);
    }
}
