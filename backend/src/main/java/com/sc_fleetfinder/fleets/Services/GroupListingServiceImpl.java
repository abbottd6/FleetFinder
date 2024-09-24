package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupListingServiceImpl implements GroupListingService {

    private final GroupListingRepository groupListingRepository;

    public GroupListingServiceImpl(GroupListingRepository groupListingRepository) {
        super();
        this.groupListingRepository = groupListingRepository;
    }

    @Override
    public List<GroupListing> getAllGroupListings() {
        return groupListingRepository.findAll();
    }

    @Override
    public GroupListing createGroupListing(GroupListing groupListing) {
        return groupListingRepository.save(groupListing);
    }

    @Override
    public GroupListing updateGroupListing(Long id, GroupListing groupListingUpdate) {
        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group Listing with id " + id + " not found"));

        groupListing.setUser(groupListingUpdate.getUser());
        groupListing.setServer(groupListingUpdate.getServer());
        groupListing.setEnvironment(groupListingUpdate.getEnvironment());
        groupListing.setExperience(groupListingUpdate.getExperience());
        groupListing.setListingTitle(groupListingUpdate.getListingTitle());
        groupListing.setListingUser(groupListingUpdate.getListingUser());
        groupListing.setPlayStyle(groupListingUpdate.getPlayStyle());
        groupListing.setLegality(groupListingUpdate.getLegality());
        groupListing.setGroupStatus(groupListingUpdate.getGroupStatus());
        groupListing.setEventSchedule(groupListingUpdate.getEventSchedule());
        groupListing.setCategory(groupListingUpdate.getCategory());
        groupListing.setSubcategory(groupListingUpdate.getSubcategory());
        groupListing.setPvpStatus(groupListingUpdate.getPvpStatus());
        groupListing.setSystem(groupListingUpdate.getSystem());
        groupListing.setPlanetMoonSystem(groupListingUpdate.getPlanetMoonSystem());
        groupListing.setListingDescription(groupListingUpdate.getListingDescription());
        groupListing.setDesiredPartySize(groupListingUpdate.getDesiredPartySize());
        groupListing.setAvailableRoles(groupListingUpdate.getAvailableRoles());
        groupListing.setCommsOption(groupListingUpdate.getCommsOption());
        groupListing.setCommsService(groupListingUpdate.getCommsService());
        return groupListingRepository.save(groupListing);
    }

    @Override
    public void deleteGroupListing(GroupListing groupListing) {
        groupListingRepository.delete(groupListing);
    }

    @Override
    public GroupListing getGroupListingById(Long id) {
        Optional<GroupListing> groupListing = groupListingRepository.findById(id);
        if(groupListing.isPresent()) {
            return groupListing.get();
        }
        else {
            throw new ResourceNotFoundException("GroupListing with id " + id + " not found");
        }
    }
}
