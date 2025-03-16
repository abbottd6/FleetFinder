package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupStatusService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupStatusConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupStatusCachingServiceImpl implements GroupStatusCachingService {

    private static final Logger log = LoggerFactory.getLogger(GroupStatusCachingServiceImpl.class);
    private final GroupStatusRepository groupStatusRepository;
    private final GroupStatusConversionService groupStatusConversionService;

    @Autowired
    public GroupStatusCachingServiceImpl(GroupStatusRepository groupStatusRepository,
                                         GroupStatusConversionService groupStatusConversionService) {
        this.groupStatusRepository = groupStatusRepository;
        this.groupStatusConversionService = groupStatusConversionService;
    }

    @Override
    @Cacheable(value = "groupStatusesCache", key = "'allGroupstatuses'")
    public List<GroupStatusDto> cacheAllGroupStatuses() {
        List<GroupStatus> groupStatuses = groupStatusRepository.findAll();

        if(groupStatuses.isEmpty()) {
            log.error("Unable to access Group Status data for caching.");
            throw new ResourceNotFoundException("Unable to access data for group statuses");
        }

        return groupStatuses.stream()
                .map(groupStatusConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
