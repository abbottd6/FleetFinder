package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrEditListingTemplateDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingTemplate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TemplateConversionServiceImpl implements TemplateConversionService {

    private final ModelMapper templatesMapper;

    public TemplateConversionServiceImpl(@Qualifier("listingTemplateMapper") ModelMapper templatesMapper) {
        this.templatesMapper = templatesMapper;
    }

    @Override
    public ListingTemplateResponseDto convertToDto(ListingTemplate entity) {
        return templatesMapper.map(entity, ListingTemplateResponseDto.class);
    }

    @Override
    public ListingTemplate convertToEntity(CreateOrEditListingTemplateDto dto) {
        return templatesMapper.map(dto, ListingTemplate.class);
    }
}
