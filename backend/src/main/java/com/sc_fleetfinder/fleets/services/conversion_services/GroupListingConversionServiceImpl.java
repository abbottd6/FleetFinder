package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupListingConversionServiceImpl implements GroupListingConversionService {

    private final ModelMapper groupListingResponseDtoMapper;
    private final ModelMapper createGroupListingModelMapper;

    public GroupListingConversionServiceImpl(ModelMapper groupListingResponseDtoMapper,
                                             ModelMapper createGroupListingModelMapper) {
        this.groupListingResponseDtoMapper = groupListingResponseDtoMapper;
        this.createGroupListingModelMapper = createGroupListingModelMapper;
    }

    @Override
    public GroupListingResponseDto convertListingToResponseDto(GroupListing groupListing) {
        return groupListingResponseDtoMapper.map(groupListing, GroupListingResponseDto.class);
    }


    //need to create an updatelisting version of this
    @Override
    public GroupListing convertToEntity(CreateGroupListingDto createGroupListingDto) {
        return createGroupListingModelMapper.map(createGroupListingDto, GroupListing.class);
    }
}
