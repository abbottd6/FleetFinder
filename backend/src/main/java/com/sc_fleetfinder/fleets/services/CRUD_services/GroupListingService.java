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

public interface GroupListingService {

    Page<GroupListingResponseDto> searchGroupListings(SearchListingsDto dto, Pageable pageable);

    List<GroupListingResponseDto> getAllGroupListings();

    @Validated
    ResponseEntity<?> createGroupListing(@Valid CreateGroupListingDto createGroupListingDto);

    @Validated
    GroupListing updateGroupListing(@Valid UpdateGroupListingDto updateGroupListingDto);

    @Validated
    ResponseEntity<?> deleteGroupListing(Long groupId, Users user);

    GroupListingResponseDto getGroupListingById(Long id);
}
