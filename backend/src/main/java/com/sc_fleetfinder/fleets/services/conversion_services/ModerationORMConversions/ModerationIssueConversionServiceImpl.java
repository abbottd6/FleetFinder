package com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ModerationIssueConversionServiceImpl implements ModerationIssueConversionService {

    private final ModelMapper modIssueEntityToDtoMapper;

    ModerationIssueConversionServiceImpl(ModelMapper modIssueEntityToDtoMapper) {
        this.modIssueEntityToDtoMapper = modIssueEntityToDtoMapper;
    }

    @Override
    public ModerationIssueResponseDto convertToResponseDto(ModerationIssue entity) {
        return modIssueEntityToDtoMapper.map(entity, ModerationIssueResponseDto.class);
    }
}
