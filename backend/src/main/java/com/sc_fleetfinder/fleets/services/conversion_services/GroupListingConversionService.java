package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;

public interface GroupListingConversionService {

    GroupListingResponseDto convertListingToResponseDto(GroupListing entity);

    GroupListing convertToEntity(CreateGroupListingDto createGroupListingDto);
}
