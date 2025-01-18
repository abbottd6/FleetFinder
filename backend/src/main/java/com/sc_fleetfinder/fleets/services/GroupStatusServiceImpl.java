package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupStatusServiceImpl implements GroupStatusService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final GroupStatusRepository groupStatusRepository;
    private final ModelMapper modelMapper;

    public GroupStatusServiceImpl(GroupStatusRepository groupStatusRepository) {
        super();
        this.groupStatusRepository = groupStatusRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "groupStatusesCache", key = "'allGroupstatuses'")
    public List<GroupStatusDto> getAllGroupStatuses() {
        log.info("Caching test: getting all group statuses");
        List<GroupStatus> groupStatuses = groupStatusRepository.findAll();
        return groupStatuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GroupStatusDto getGroupStatusById(int id) {
        Optional<GroupStatus> groupStatus = groupStatusRepository.findById(id);
        if (groupStatus.isPresent()) {
            return convertToDto(groupStatus.get());
        }
        else {
            throw new ResourceNotFoundException("Group with id " + id + " not found");
        }
    }

    public GroupStatusDto convertToDto(GroupStatus entity) {
        return modelMapper.map(entity, GroupStatusDto.class);
    }

    public GroupStatus convertToEntity(GroupStatusDto dto) {
        return modelMapper.map(dto, GroupStatus.class);
    }

}
