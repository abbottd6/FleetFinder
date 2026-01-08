package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingTemplate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TemplateConversionServiceImpl implements TemplateConversionService {

    private final ModelMapper templateDtoMapper;
    private final ModelMapper templateEntityMapper;

    public TemplateConversionServiceImpl(@Qualifier("templateDtoMapper") ModelMapper templateDtoMapper,
                                         @Qualifier("templateEntityMapper") ModelMapper templateEntityMapper) {
        this.templateDtoMapper = templateDtoMapper;
        this.templateEntityMapper = templateEntityMapper;
    }

    @Override
    public ListingTemplateResponseDto convertToDto(ListingTemplate entity) {
        return templateDtoMapper.map(entity, ListingTemplateResponseDto.class);
    }

    @Override
    public ListingTemplate convertToEntity(CreateGroupListingDto dto) {
        return templateEntityMapper.map(dto, ListingTemplate.class);
    }
}
