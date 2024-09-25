package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.User;
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
    private final UserRepository userRepository;


    public GroupListingServiceImpl(GroupListingRepository groupListingRepository, ModelMapper modelMapper, UserRepository userRepository) {
        super();
        this.groupListingRepository = groupListingRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<GroupListingDto> getAllGroupListings() {
        List<GroupListing> groupListings = groupListingRepository.findAll();
        return groupListings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GroupListing createGroupListing(@Valid GroupListingDto groupListingDto) {
        Objects.requireNonNull(groupListingDto, "GroupListingDto cannot be null");

            GroupListing groupListing = convertToEntity(groupListingDto);
            return groupListingRepository.save(groupListing);
    }

    @Override
    public GroupListing updateGroupListing(Long id,@Valid GroupListingDto groupListingDto) {
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
    public GroupListingDto getGroupListingById(Long id) {
        Optional<GroupListing> groupListing = groupListingRepository.findById(id);
        if(groupListing.isPresent()) {
            return convertToDto(groupListing.get());
        }
        else {
            throw new ResourceNotFoundException("GroupListing with id " + id + " not found");
        }
    }

    public GroupListingDto convertToDto(GroupListing groupListing) {
        return modelMapper.map(groupListing, GroupListingDto.class);
    }

    public GroupListing convertToEntity(GroupListingDto groupListingDto) {
        return modelMapper.map(groupListingDto, GroupListing.class);
    }
}
