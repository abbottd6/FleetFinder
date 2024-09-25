package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupListingServiceImpl implements GroupListingService {

    private final GroupListingRepository groupListingRepository;
    private final ModelMapper modelMapper;


    public GroupListingServiceImpl(GroupListingRepository groupListingRepository, ModelMapper modelMapper) {
        super();
        this.groupListingRepository = groupListingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GroupListingDto> getAllGroupListings() {
        List<GroupListing> groupListings = groupListingRepository.findAll();
        return groupListings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GroupListing createGroupListing(GroupListingDto groupListingDto) {
        if(groupListingDto != null){
            GroupListing groupListing = convertToEntity(groupListingDto);
            return groupListingRepository.save(groupListing);
        }
        else {
            throw new IllegalArgumentException("GroupListingDto is null");
        }
    }

    @Override
    public GroupListing updateGroupListing(Long id, GroupListingDto groupListingDto) {

        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing with id " + id + " not found"));

        BeanUtils.copyProperties(groupListingDto, groupListing, "groupId", "user", "listingUser");

        return groupListingRepository.save(groupListing);
    }

    @Override
    public void deleteGroupListing(Long id) {
        groupListingRepository.delete(groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing with id " + id + "not found")));
    }

    @Override
    public GroupListingDto getGroupListingById(Long id) {
        Optional<GroupListing> groupListing = groupListingRepository.findById(id);
        if(groupListing.isPresent()) {
            return modelMapper.map(groupListing, GroupListingDto.class);
        }
        else {
            throw new ResourceNotFoundException("GroupListing with id " + id + " not found");
        }
    }

    private GroupListingDto convertToDto(GroupListing groupListing) {
        return modelMapper.map(groupListing, GroupListingDto.class);
    }

    private GroupListing convertToEntity(GroupListingDto groupListingDto) {
        return modelMapper.map(groupListingDto, GroupListing.class);
    }
}
