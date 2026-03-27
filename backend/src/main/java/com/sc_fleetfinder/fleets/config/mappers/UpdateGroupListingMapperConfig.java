package com.sc_fleetfinder.fleets.config.mappers;


import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.AbstractConverter;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Configuration
public class UpdateGroupListingMapperConfig {

    private final MapperLookupService mapperLookupService;

    public UpdateGroupListingMapperConfig(MapperLookupService mapperLookupService) {

        this.mapperLookupService = mapperLookupService;
    }

    @Bean
    public ModelMapper updateGroupListingModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<UpdateGroupListingDto, Instant> dateTimeAndZoneToInstantConverter = ctx -> {
            UpdateGroupListingDto src = ctx.getSource();
            if(src == null) return null;

            String dateStr = src.getEventDate();
            String timeStr = src.getEventTime();
            String zoneStr = src.getEventTimeZone();

            if(dateStr == null || timeStr == null || zoneStr == null) return null;
            if(dateStr.isBlank() || timeStr.isBlank() || zoneStr.isBlank()) return null;

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            ZoneId zone = ZoneId.of(zoneStr);

            return ZonedDateTime.of(date, time, zone).toInstant();
        };

        modelMapper.createTypeMap(UpdateGroupListingDto.class, GroupListing.class)
                .addMappings(mapper -> {

                    //skipping fixed fields
                    mapper.skip(GroupListing::setGroupId);
                    mapper.skip(GroupListing::setCreationTimestamp);
                    mapper.skip(GroupListing::setUsers);

                    mapper.using((MappingContext<Integer, ServerRegion> ctx) ->
                                    mapperLookupService.findServerRegionById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getServerId, GroupListing::setServer);

                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) ->
                                    mapperLookupService.findEnvironmentById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getEnvironmentId, GroupListing::setEnvironment);

                    mapper.using((MappingContext<Integer, GameExperience> ctx) ->
                                    mapperLookupService.findExperienceById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getExperienceId, GroupListing::setExperience);

                    //listing title mapped automatically by model mapper due to property name and type match

                    //playStyleId to playStyle entity
                    mapper.using((MappingContext<Integer, PlayStyle> ctx) -> {
                        Integer playStyleId = ctx.getSource();
                        if (playStyleId == null) {
                            return null;
                        }
                        return mapperLookupService.findPlayStyleById(ctx.getSource());
                    }).map(UpdateGroupListingDto::getPlayStyleId, GroupListing::setPlayStyle);

                    //legalityId to legality entity
                    mapper.using((MappingContext<Integer, Legality> ctx) -> mapperLookupService.findLegalityById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getLegalityId, GroupListing::setLegality);

                    //groupStatusId to groupStatus entity
                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mapperLookupService.findGroupStatusById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getGroupStatusId, GroupListing::setGroupStatus);

                    //eventScheduleDate mapped to eventSchedule Instant
                    mapper.using(dateTimeAndZoneToInstantConverter).map(src -> src, GroupListing::setEventSchedule);

                    //categoryId to category entity
                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mapperLookupService.findCategoryById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getCategoryId, GroupListing::setCategory);

                    //subcategoryId to subcategory entity
                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> {
                        Integer subcategoryId = ctx.getSource();
                        if (subcategoryId == null) {
                            return null;
                        }
                        return mapperLookupService.findSubcategoryById(ctx.getSource());
                    }).map(UpdateGroupListingDto::getSubcategoryId, GroupListing::setSubcategory);

                    //pvpStatusId to pvp status entity
                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mapperLookupService.findPvpStatusById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getPvpStatusId, GroupListing::setPvpStatus);

                    //systemId to planetary system entity
                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mapperLookupService.findPlanetarySystemById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getSystemId, GroupListing::setSystem);

                    //planetId to planet moon system entity
                    mapper.using((MappingContext<Integer, PlanetMoonSystem> ctx) -> {
                        Integer planetId = ctx.getSource();
                        if (planetId == null) {
                            return null;
                        }

                        return mapperLookupService.findPlanetMoonSystemById(planetId);
                    }).map(UpdateGroupListingDto::getPlanetId, GroupListing::setPlanetMoonSystem);

                    //listing description mapped automatically due to property name and type match

                    //desired party size mapped automatically due to property name and type match

                    //current party size mapped automatically due to property name and type match

                    //available roles mapped automatically due to property name and type match

                    //comms option mapped automatically due to property name and type match

                    //comms service mapped automatically due to property name and type match

                    //language code mapped automatically due to property name and type match
                });
        return modelMapper;
    }
}
