package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.Services.ServerRegionServiceImpl;
import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        GroupListing groupListingEntity = convertToEntity(groupListingDto);

        groupListing.setUser(groupListingEntity.getUser());
        groupListing.setServer(groupListingEntity.getServer());
        groupListing.setEnvironment(groupListingEntity.getEnvironment());
        groupListing.setExperience(groupListingEntity.getExperience());
        groupListing.setListingTitle(groupListingEntity.getListingTitle());
        groupListing.setListingUser(groupListingEntity.getListingUser());
        groupListing.setPlayStyle(groupListingEntity.getPlayStyle());
        groupListing.setLegality(groupListingEntity.getLegality());
        groupListing.setGroupStatus(groupListingEntity.getGroupStatus());
        groupListing.setEventSchedule(groupListingEntity.getEventSchedule());
        groupListing.setCategory(groupListingEntity.getCategory());
        groupListing.setSubcategory(groupListingEntity.getSubcategory());
        groupListing.setPvpStatus(groupListingEntity.getPvpStatus());
        groupListing.setSystem(groupListingEntity.getSystem());
        groupListing.setPlanetMoonSystem(groupListingEntity.getPlanetMoonSystem());
        groupListing.setListingDescription(groupListingEntity.getListingDescription());
        groupListing.setDesiredPartySize(groupListingEntity.getDesiredPartySize());
        groupListing.setAvailableRoles(groupListingEntity.getAvailableRoles());
        groupListing.setCommsOption(groupListingEntity.getCommsOption());
        groupListing.setCommsService(groupListingEntity.getCommsService());

        return groupListingRepository.save(groupListing);

    }

    @Override
    public void deleteGroupListing(GroupListing groupListing) {
        groupListingRepository.delete(groupListing);
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
