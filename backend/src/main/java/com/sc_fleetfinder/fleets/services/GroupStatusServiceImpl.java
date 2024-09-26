package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupStatusServiceImpl implements GroupStatusService {

    private final GroupStatusRepository groupStatusRepository;
    private final ModelMapper modelMapper;

    public GroupStatusServiceImpl(GroupStatusRepository groupStatusRepository) {
        super();
        this.groupStatusRepository = groupStatusRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<GroupStatusDto> getAllGroupStatuses() {
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
