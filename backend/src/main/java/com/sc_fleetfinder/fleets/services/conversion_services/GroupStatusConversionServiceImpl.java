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
            log.error("Group status convertToDto encountered an Id that is null or 0.");
            throw new IllegalArgumentException("Group status id is null or 0");
        }

        //name valid check
        if(entity.getGroupStatus() == null || entity.getGroupStatus().isEmpty()) {
            log.error("Group status convertToDto encountered a name that is null or empty.");
            throw new IllegalArgumentException("Group status name field is null or empty");
        }

        return modelMapper.map(entity, GroupStatusDto.class);
    }

    @Override
    public GroupStatus convertToEntity(GroupStatusDto dto) {
        //checking repository for entity that matches Dto id
        GroupStatus entity = groupStatusRepository.findById(dto.getGroupStatusId())
                .orElseGet(() -> {
                    log.error("Group status convertToEntity could not find an entity with Id: {}",
                            dto.getGroupStatusId());
                    throw new ResourceNotFoundException("Group status with ID: " + dto.getGroupStatusId() +
                            " not found");
                });

        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getGroupStatus(), entity.getGroupStatus())) {
            log.error("Group status convertToEntity encountered an id/name mismatch for dto with Id: {}, and " +
                    "status: {}", dto.getGroupStatusId(), dto.getGroupStatus());
            throw new ResourceNotFoundException("Group status name mismatch for Dto: " + dto.getGroupStatus() +
                    ", ID: " + dto.getGroupStatusId() + " and entity: " + entity.getGroupStatus() +
                    ", ID: " + entity.getGroupStatusId());
        }
        //if Id exists and names match then returns entity
        return entity;
    }
}
