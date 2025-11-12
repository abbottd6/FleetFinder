package com.sc_fleetfinder.fleets.services.mod_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface ModerationService {

    List<GroupListingResponseDto> modGetAllGroupListings();

    ResponseEntity<?> modDeleteListing(@Valid DeleteGroupListingDto modDeleteDto);
}
