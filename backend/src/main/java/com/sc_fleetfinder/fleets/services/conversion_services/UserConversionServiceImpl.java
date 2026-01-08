package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserConversionServiceImpl implements UserConversionService{

    private static final Logger log = LoggerFactory.getLogger(UserConversionServiceImpl.class);
    private final ModelMapper groupListingResposneDtoMapper;
    private final ModelMapper modelMapper;

    @Autowired
    public UserConversionServiceImpl(ModelMapper groupListingResponseDtoMapper,
                                     ModelMapper modelMapper) {
        this.groupListingResposneDtoMapper = groupListingResponseDtoMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public PrivateUserResponseDto convertToPrivateDto(Users users) {

        //Entity 'Users' contains a set of groupListing entities that also need to be converted to the response dto
        Set<GroupListingResponseDto> groupListingResponseDtos = users.getGroupListings().stream()
                .map(groupListing -> groupListingResposneDtoMapper.map(groupListing, GroupListingResponseDto.class))
                .collect(Collectors.toSet());

        PrivateUserResponseDto privateUserResponseDto = modelMapper.map(users, PrivateUserResponseDto.class);

        //Setting the converted groupListingDtos from above as the privateUserResponseDto's set of group listings
        privateUserResponseDto.setGroupListingsDto(groupListingResponseDtos);

        return privateUserResponseDto;
    }

    @Override
    public PublicUserResponseDto convertToPublicDto(Users users) {

        return modelMapper.map(users, PublicUserResponseDto.class);
    }
}
