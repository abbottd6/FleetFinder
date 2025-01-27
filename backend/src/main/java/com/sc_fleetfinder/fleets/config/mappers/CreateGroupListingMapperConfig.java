package com.sc_fleetfinder.fleets.config.mappers;


import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.User;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;



@Configuration
public class CreateGroupListingMapperConfig {

    private final MapperLookupService mapperLookupService;

    public CreateGroupListingMapperConfig(MapperLookupService mapperLookupService) {

        this.mapperLookupService = mapperLookupService;
    }

    @Bean
    public ModelMapper createGroupListingModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //converter for date time strings to Instant type
        modelMapper.addConverter(new AbstractConverter<String, Instant>() {
            @Override
            protected Instant convert(String source) {
                return source != null ? Instant.parse(source) : null;
            }
        });

            modelMapper.createTypeMap(CreateGroupListingDto.class, GroupListing.class)
                    .addMappings(mapper -> {

                        //skipping auto-generated fields
                        mapper.skip(GroupListing::setGroupId);
                        mapper.skip(GroupListing::setCreationTimestamp);
                        mapper.skip(GroupListing::setLastUpdated);

                        //userId to user entity
                        mapper.using((MappingContext<Long, User> ctx) -> mapperLookupService.findUserById(ctx.getSource()))
                                .map(CreateGroupListingDto::getUserId, GroupListing::setUser);

                        //serverId to server entity
                        mapper.using((MappingContext<Integer, ServerRegion> ctx) -> mapperLookupService.findServerRegionById(ctx.getSource()))
                                .map(CreateGroupListingDto::getServerId, GroupListing::setServer);

                        //environmentId to environment entity
                        mapper.using((MappingContext<Integer, GameEnvironment> ctx) -> mapperLookupService.findEnvironmentById(ctx.getSource()))
                                .map(CreateGroupListingDto::getEnvironmentId, GroupListing::setEnvironment);

                        //experienceId to experience entity
                        mapper.using((MappingContext<Integer, GameExperience> ctx) -> mapperLookupService.findExperienceById(ctx.getSource()))
                                .map(CreateGroupListingDto::getExperienceId, GroupListing::setExperience);

                        //listing title mapped automatically by model mapper due to property name and type match

                        //playStyleId to playStyle entity
                        mapper.using((MappingContext<Integer, PlayStyle> ctx) -> mapperLookupService.findPlayStyleById(ctx.getSource()))
                                .map(CreateGroupListingDto::getPlayStyleId, GroupListing::setPlayStyle);

                        //legalityId to legality entity
                        mapper.using((MappingContext<Integer, Legality> ctx) -> mapperLookupService.findLegalityById(ctx.getSource()))
                                .map(CreateGroupListingDto::getLegalityId, GroupListing::setLegality);

                        //groupStatusId to groupStatus entity
                        mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mapperLookupService.findGroupStatusById(ctx.getSource()))
                                .map(CreateGroupListingDto::getGroupStatusId, GroupListing::setGroupStatus);

                        //eventScheduleDate mapped to eventSchedule Instant
                        mapper.map(CreateGroupListingDto::getEventSchedule, GroupListing::setEventSchedule);

                        //categoryId to category entity
                        mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mapperLookupService.findCategoryById(ctx.getSource()))
                                .map(CreateGroupListingDto::getCategoryId, GroupListing::setCategory);

                        //subcategoryId to subcategory entity
                        mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> mapperLookupService.findSubcategoryById(ctx.getSource()))
                                .map(CreateGroupListingDto::getSubcategoryId, GroupListing::setSubcategory);

                        //pvpStatusId to pvp status entity
                        mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mapperLookupService.findPvpStatusById(ctx.getSource()))
                                .map(CreateGroupListingDto::getPvpStatusId, GroupListing::setPvpStatus);

                        //systemId to planetary system entity
                        mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mapperLookupService.findPlanetarySystemById(ctx.getSource()))
                                .map(CreateGroupListingDto::getSystemId, GroupListing::setSystem);

                        //planetId to planet moon system entity
                        mapper.using((MappingContext<Integer, PlanetMoonSystem> ctx) -> mapperLookupService.findPlanetMoonSystemById(ctx.getSource()))
                                .map(CreateGroupListingDto::getPlanetId, GroupListing::setPlanetMoonSystem);

                        //listing description mapped automatically due to property name and type match

                        //desired party size mapped automatically due to property name and type match

                        //current party size mapped automatically due to property name and type match

                        //available roles mapped automatically due to property name and type match

                        //comms option mapped automatically due to property name and type match

                        //comms service mapped automatically due to property name and type match
                    });
        return modelMapper;
    }
}
