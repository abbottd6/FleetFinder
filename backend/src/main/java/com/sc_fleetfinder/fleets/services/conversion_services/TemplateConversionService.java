package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingTemplate;

public interface TemplateConversionService {

    ListingTemplateResponseDto convertToDto(ListingTemplate entity);
    ListingTemplate convertToEntity(CreateGroupListingDto dto);
}
