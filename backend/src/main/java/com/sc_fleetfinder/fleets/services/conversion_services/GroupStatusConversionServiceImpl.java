package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GroupStatusConversionServiceImpl implements GroupStatusConversionService {

    private static final Logger log = LoggerFactory.getLogger(GroupStatusConversionServiceImpl.class);
    private final GroupStatusRepository groupStatusRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GroupStatusConversionServiceImpl(GroupStatusRepository groupStatusRepository) {
        this.groupStatusRepository = groupStatusRepository;
        this.modelMapper = new ModelMapper();
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

    @Override
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
