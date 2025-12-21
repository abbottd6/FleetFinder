package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupListingConversionServiceImpl implements GroupListingConversionService {

    private final ModelMapper createGroupListingModelMapper;
    private final ModelMapper updateGroupListingModelMapper;
    private final ModelMapper modelMapper;

    public GroupListingConversionServiceImpl(ModelMapper createGroupListingModelMapper,
                                             ModelMapper updateGroupListingModelMapper,
                                             ModelMapper modelMapper) {
        this.createGroupListingModelMapper = createGroupListingModelMapper;
        this.updateGroupListingModelMapper = updateGroupListingModelMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public GroupListingResponseDto convertListingToResponseDto(GroupListing groupListing) {
        return modelMapper.map(groupListing, GroupListingResponseDto.class);
    }

    //need to create an updatelisting version of this
    @Override
    public GroupListing convertToEntity(CreateGroupListingDto createGroupListingDto) {
        return createGroupListingModelMapper.map(createGroupListingDto, GroupListing.class);
    }

    @Override
    public GroupListing convertToEntity(UpdateGroupListingDto updateGroupListingDto) {
        return updateGroupListingModelMapper.map(updateGroupListingDto, GroupListing.class);
    }
}
