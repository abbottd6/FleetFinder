package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupListingServiceImpl implements GroupListingService {

    private final GroupListingRepository groupListingRepository;
    private final ModelMapper createGroupListingModelMapper;
    private final ModelMapper groupListingResponseDtoModelMapper;
    private final ModelMapper modelMapper;


    public GroupListingServiceImpl(GroupListingRepository groupListingRepository,
                     @Qualifier("createGroupListingModelMapper") ModelMapper createGroupListingModelMapper,
                     @Qualifier("groupListingResponseDtoMapper") ModelMapper groupListingResponseDtoModelMapper) {
        super();

        this.modelMapper = new ModelMapper();
        this.groupListingRepository = groupListingRepository;
        this.createGroupListingModelMapper = createGroupListingModelMapper;
        this.groupListingResponseDtoModelMapper = groupListingResponseDtoModelMapper;
    }

    @Override
    public List<GroupListingResponseDto> getAllGroupListings() {
        List<GroupListing> groupListings = groupListingRepository.findAll();
        return groupListings.stream()
                .map(this::convertListingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> createGroupListing(@Valid CreateGroupListingDto createGroupListingDto) {
        Objects.requireNonNull(createGroupListingDto, "GroupListingResponseDto cannot be null");
            try {
                GroupListing groupListing = createGroupListingModelMapper.map(createGroupListingDto, GroupListing.class);

                groupListingRepository.save(groupListing);

                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", groupListing.getListingTitle());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while creating the listing.");
            }
    }

    @Override
    public GroupListing updateGroupListing(@PathVariable Long id, @Valid UpdateGroupListingDto updateGroupListingDto) {
        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing with id " + id + " not found"));


        //ADD LOGIC TO CHECK THE NUMBER OF GROUPLISTINGS ASSOCIATED WITH A USER.
        //LIMIT THE NUMBER OF GROUP LISTINGS PER USER TO 3
        //ADD LOGIC TO ADD this.groupListing TO THE USER's SET OF GROUPLISTINGS AS IT GETS CREATED


        BeanUtils.copyProperties(updateGroupListingDto, groupListing, "groupId", "user", "listingUser");

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
        return groupListingResponseDtoModelMapper.map(groupListing, GroupListingResponseDto.class);
    }


    //need to create an updatelisting version of this
    public GroupListing convertToEntity(CreateGroupListingDto createGroupListingDto) {
        return modelMapper.map(createGroupListingDto, GroupListing.class);
    }
}
