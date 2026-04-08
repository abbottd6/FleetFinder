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

    private final ModelMapper modelMapper;

    public TemplateConversionServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ListingTemplateResponseDto convertToDto(ListingTemplate entity) {
        return modelMapper.map(entity, ListingTemplateResponseDto.class);
    }

    @Override
    public ListingTemplate convertToEntity(CreateOrEditListingTemplateDto dto) {
        return modelMapper.map(dto, ListingTemplate.class);
    }
}
