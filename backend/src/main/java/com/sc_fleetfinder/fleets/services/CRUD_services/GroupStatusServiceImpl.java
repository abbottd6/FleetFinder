package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.GroupStatusCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupStatusServiceImpl implements GroupStatusService {

    private static final Logger log = LoggerFactory.getLogger(GroupStatusServiceImpl.class);
    private final GroupStatusCachingService groupStatusCachingService;

    public GroupStatusServiceImpl(GroupStatusCachingService groupStatusCachingService) {
        this.groupStatusCachingService = groupStatusCachingService;
    }

    @Override
    public List<GroupStatusDto> getAllGroupStatuses() {
        return groupStatusCachingService.cacheAllGroupStatuses();
    }

    @Override
    public GroupStatusDto getGroupStatusById(Integer id) {
        List<GroupStatusDto> cachedGroupStatuses = groupStatusCachingService.cacheAllGroupStatuses();

        return cachedGroupStatuses.stream()
                .filter(groupStatus -> groupStatus.getGroupStatusId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Group Status with id {} not found", id);
                    return new ResourceNotFoundException(id);
                });
    }
}
