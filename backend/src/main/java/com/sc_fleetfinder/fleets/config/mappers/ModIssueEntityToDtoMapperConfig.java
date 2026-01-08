package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class ModIssueEntityToDtoMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            .withZone(ZoneOffset.UTC);

    public ModIssueEntityToDtoMapperConfig() {}

    @Bean
    public ModelMapper modIssueEntityToDtoMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

        modelMapper.createTypeMap(ModerationIssue.class, ModerationIssueResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(ModerationIssue::getIssueId, ModerationIssueResponseDto::setIssueId);

                    mapper.using(ctx -> {
                        GroupListing listing = (GroupListing) ctx.getSource();
                        return listing != null ? listing.getGroupId() : null;
                    }).map(ModerationIssue::getGroupRef, ModerationIssueResponseDto::setGroupId);

                    mapper.using(ctx -> {
                        Users user = (Users) ctx.getSource();
                        return user != null ? user.getUsername() : null;
                    }).map(ModerationIssue::getUserRef, ModerationIssueResponseDto::setUsername);

                    mapper.using(ctx -> {
                        Users user = (Users) ctx.getSource();
                        return user != null ? user.getUserId() : null;
                    }).map(ModerationIssue::getUserRef, ModerationIssueResponseDto::setUserId);

                    mapper.map(ModerationIssue::getReportTotalCount, ModerationIssueResponseDto::setReportTotalCount);

                    mapper.map(ModerationIssue::getSpamCount, ModerationIssueResponseDto::setSpamCount);

                    mapper.map(ModerationIssue::getHateSpeechCount, ModerationIssueResponseDto::setHateSpeechCount);

                    mapper.map(ModerationIssue::getNsfwCount, ModerationIssueResponseDto::setNsfwCount);

                    mapper.map(ModerationIssue::getScamCount, ModerationIssueResponseDto::setScamCount);

                    mapper.map(ModerationIssue::getOffTopicCount, ModerationIssueResponseDto::setOffTopicCount);

                    mapper.map(ModerationIssue::getTrollCount, ModerationIssueResponseDto::setTrollCount);

                    mapper.map(ModerationIssue::getDoxxCount, ModerationIssueResponseDto::setDoxxCount);

                    mapper.map(ModerationIssue::getCheatCount, ModerationIssueResponseDto::setCheatCount);

                    mapper.map(ModerationIssue::getOtherCount, ModerationIssueResponseDto::setOtherCount);

                    mapper.map(ModerationIssue::getFirstReportTs, ModerationIssueResponseDto::setFirstReportTs);

                    mapper.map(ModerationIssue::getLastReportTs, ModerationIssueResponseDto::setLastReportTs);

                    mapper.map(ModerationIssue::getStatus, ModerationIssueResponseDto::setStatus);
                });

        return modelMapper;
    }
}
