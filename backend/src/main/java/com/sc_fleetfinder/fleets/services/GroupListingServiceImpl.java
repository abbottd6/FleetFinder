package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupListingServiceImpl implements GroupListingService {

    private final GroupListingRepository groupListingRepository;
    private final ModelMapper modelMapper;


    public GroupListingServiceImpl(GroupListingRepository groupListingRepository, ModelMapper modelMapper) {
        super();
        this.groupListingRepository = groupListingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GroupListingResponseDto> getAllGroupListings() {
        List<GroupListing> groupListings = groupListingRepository.findAll();
        return groupListings.stream()
                .map(this::convertListingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GroupListing createGroupListing(@Valid GroupListingResponseDto groupListingDto) {
        Objects.requireNonNull(groupListingDto, "GroupListingResponseDto cannot be null");

            GroupListing groupListing = convertToEntity(groupListingDto);
            return groupListingRepository.save(groupListing);
    }

    @Override
    public GroupListing updateGroupListing(Long id,@Valid GroupListingResponseDto groupListingDto) {
        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing with id " + id + " not found"));


        //ADD LOGIC TO CHECK THE NUMBER OF GROUPLISTINGS ASSOCIATED WITH A USER.
        //LIMIT THE NUMBER OF GROUP LISTINGS PER USER TO 3
        //ADD LOGIC TO ADD this.groupListing TO THE USER's SET OF GROUPLISTINGS AS IT GETS CREATED


        BeanUtils.copyProperties(groupListingDto, groupListing, "groupId", "user", "listingUser");

        return groupListingRepository.save(groupListing);
    }

    @Override
    public void deleteGroupListing(Long id) {

        //ADD LOGIC TO CHECK THE NUMBER OF GROUPLISTINGS ASSOCIATED WITH A USER.
        //LIMIT THE NUMBER OF GROUP LISTINGS PER USER TO 3
        //ADD LOGIC TO REMOVE this.groupListing FROM THE USER's SET OF GROUPLISTINGS AS IT GETS DELETED

        groupListingRepository.delete(groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing with id " + id + "not found")));
    }

    @Override
    public GroupListingResponseDto getGroupListingById(Long id) {
        Optional<GroupListing> groupListing = groupListingRepository.findById(id);
        if(groupListing.isPresent()) {
            return convertListingToDto(groupListing.get());
        }
        else {
            throw new ResourceNotFoundException("GroupListing with id " + id + " not found");
        }
    }

    public GroupListingResponseDto convertListingToDto(GroupListing groupListing) {
        return modelMapper.map(groupListing, GroupListingResponseDto.class);
    }

    public GroupListing convertToEntity(GroupListingResponseDto groupListingDto) {
        return modelMapper.map(groupListingDto, GroupListing.class);
    }
}
