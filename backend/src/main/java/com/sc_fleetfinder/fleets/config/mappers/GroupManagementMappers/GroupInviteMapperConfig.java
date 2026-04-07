package com.sc_fleetfinder.fleets.config.mappers.GroupManagementMappers;


import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteRequestOrResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class GroupInviteMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern(
            "MM/dd/yy HH:mm").withZone(ZoneOffset.UTC);

    @Bean("GroupInviteMapper")
    public ModelMapper groupInviteMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

        modelMapper.createTypeMap(GroupInvite.class, GroupInviteRequestOrResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(GroupInvite::getInviteId, GroupInviteRequestOrResponseDto::setInviteId);

                    mapper.map(GroupInvite::getSender, GroupInviteRequestOrResponseDto::setSenderSummary);

                    mapper.map(GroupInvite::getRecipient, GroupInviteRequestOrResponseDto::setRecipientSummary);

                    mapper.map(GroupInvite::getGroupListing, GroupInviteRequestOrResponseDto::setListingDetails);

                    mapper.map(GroupInvite::getRosterClass, GroupInviteRequestOrResponseDto::setRosterClass);

                    mapper.map(GroupInvite::getInviteRole, GroupInviteRequestOrResponseDto::setRoleSummary);

                    mapper.map(GroupInvite::getInviteDirection, GroupInviteRequestOrResponseDto::setInviteDirection);

                    mapper.map(GroupInvite::getInviteStatus, GroupInviteRequestOrResponseDto::setInviteStatus);

                    mapper.map(GroupInvite::getInviteMessage, GroupInviteRequestOrResponseDto::setInviteMessage);

                    mapper.map(GroupInvite::getExpiresAt, GroupInviteRequestOrResponseDto::setExpiresAt);

                    mapper.map(GroupInvite::getCreatedAt, GroupInviteRequestOrResponseDto::setSentAt);
                });

        return modelMapper;
    }
}
