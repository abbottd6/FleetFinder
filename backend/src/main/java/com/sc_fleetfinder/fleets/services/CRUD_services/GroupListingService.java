package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SearchListingsDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

public interface GroupListingService {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Page<GroupListingResponseDto> searchGroupListings(SearchListingsDto dto, Pageable pageable, Optional<Users> userOpt);

    List<GroupListingResponseDto> getAllGroupListings();

    @Validated
    ResponseEntity<?> createGroupListing(@Valid CreateGroupListingDto dto, Users user);

    @Validated
    ResponseEntity<?> updateGroupListing(@Valid UpdateGroupListingDto dto, Users user);

    @Validated
    ResponseEntity<?> deleteGroupListing(Long groupId, Users user);

    GroupListingResponseDto getGroupListingDtoById(Long id);

    GroupListing findGroupListingEntityById(Long groupId);
}
