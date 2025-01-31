package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.GroupStatusCachingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupStatusServiceImpl implements GroupStatusService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final GroupStatusRepository groupStatusRepository;
    private final GroupStatusCachingService groupStatusCachingService;
    private final ModelMapper modelMapper;

    public GroupStatusServiceImpl(GroupStatusRepository groupStatusRepository,
                                  GroupStatusCachingService groupStatusCachingService) {
        super();
        this.groupStatusCachingService = groupStatusCachingService;
        this.groupStatusRepository = groupStatusRepository;
        this.modelMapper = new ModelMapper();
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
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public GroupStatusDto convertToDto(GroupStatus entity) {
        //id valid check
        if(entity.getGroupStatusId() == null || entity.getGroupStatusId() == 0) {
            throw new ResourceNotFoundException("Group status id is null or 0");
        }

        //name valid check
        if(entity.getGroupStatus() == null || entity.getGroupStatus().isEmpty()) {
            throw new ResourceNotFoundException("Group status name field is null or empty");
        }

        return modelMapper.map(entity, GroupStatusDto.class);
    }

    public GroupStatus convertToEntity(GroupStatusDto dto) {
        //checking repository for entity that matches Dto id
        GroupStatus entity = groupStatusRepository.findById(dto.getGroupStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Group status with ID: " + dto.getGroupStatusId() +
                        " not found"));

        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getGroupStatus(), entity.getGroupStatus())) {
            throw new ResourceNotFoundException("Group status name mismatch for Dto: " + dto.getGroupStatus() +
                    ", ID: " + dto.getGroupStatusId() + " and entity: " + entity.getGroupStatus() +
                    ", ID: " + entity.getGroupStatusId());
        }
        //if Id exists and names match then returns entity
        return entity;
    }

}
