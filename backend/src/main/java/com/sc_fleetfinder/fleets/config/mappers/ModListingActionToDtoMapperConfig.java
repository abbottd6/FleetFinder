package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class ModListingActionToDtoMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            .withZone(ZoneOffset.UTC);

    public ModListingActionToDtoMapperConfig() {}

    @Bean
    public ModelMapper modListingActionToDtoMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });


        modelMapper.typeMap(ModListingAction.class, ModListingActionDto.class)
                .addMappings(mapper -> mapper.map(
                        src -> src.getArchive() == null ? null : src.getArchive().getArchiveId(),
                        ModListingActionDto::setArchiveId)
                );

        return modelMapper;
    }
}
