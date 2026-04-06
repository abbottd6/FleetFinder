package com.sc_fleetfinder.fleets.config.mappers.GroupManagementMappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupInviteResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
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
public class GroupMemberMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern(
            "MM/dd/yy HH:mm").withZone(ZoneOffset.UTC);

    @Bean("GroupMemberMapper")
    public ModelMapper groupMemberMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

        modelMapper.createTypeMap(GroupMember.class, GroupMembershipResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(GroupMember::getUser, GroupMembershipResponseDto::setUserSummary);

                    mapper.map(GroupMember::getMemberStatus, GroupMembershipResponseDto::setMemberStatus);

                    mapper.map(GroupMember::getMemberNote, GroupMembershipResponseDto::setMemberNote);

                    mapper.map(GroupMember::getHasComms, GroupMembershipResponseDto::setHasComms);

                    mapper.map(GroupMember::getHasExtNotes, GroupMembershipResponseDto::setHasExtNotes);

                    mapper.map(GroupMember::getRsvpStatus, GroupMembershipResponseDto::setRsvpStatus);

                    mapper.map(GroupMember::getCreatedAt, GroupMembershipResponseDto::setJoinedAt);

                    mapper.map(GroupMember::getMemberRank, GroupMembershipResponseDto::setMemberRank);

                    mapper.map(GroupMember::getGroupListing, GroupMembershipResponseDto::setListing);
                });

        modelMapper.createTypeMap(GroupInvite.class, GroupInviteResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(GroupInvite::getSender, GroupInviteResponseDto::setSenderSummary);

                    mapper.map(GroupInvite::getRecipient, GroupInviteResponseDto::setRecipientSummary);

                    mapper.map(GroupInvite::getRosterClass, GroupInviteResponseDto::setRosterClass);

                    mapper.map(GroupInvite::getInviteRole, GroupInviteResponseDto::setRoleSummary);

                    mapper.map(GroupInvite::getInviteDirection, GroupInviteResponseDto::setInviteDirection);

                    mapper.map(GroupInvite::getInviteStatus, GroupInviteResponseDto::setInviteStatus);

                    mapper.map(GroupInvite::getInviteMessage, GroupInviteResponseDto::setInviteMessage);

                    mapper.map(GroupInvite::getExpiresAt, GroupInviteResponseDto::setExpiresAt);

                    mapper.map(GroupInvite::getCreatedAt, GroupInviteResponseDto::setSentAt);
                });

        return modelMapper;
    }
}
