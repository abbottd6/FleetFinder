package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupStatusServiceImpl implements GroupStatusService {

    private final GroupStatusRepository groupStatusRepository;

    public GroupStatusServiceImpl(GroupStatusRepository groupStatusRepository) {
        super();
        this.groupStatusRepository = groupStatusRepository;
    }

    @Override
    public List<GroupStatus> getAllGroupStatuses() {
        return groupStatusRepository.findAll();
    }

    @Override
    public GroupStatus getGroupStatusById(int id) {
        Optional<GroupStatus> groupStatus = groupStatusRepository.findById(id);
        if (groupStatus.isPresent()) {
            return groupStatus.get();
        }
        else {
            throw new ResourceNotFoundException("Group with id " + id + " not found");
        }
    }

}
