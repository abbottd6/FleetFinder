package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingTemplateResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.ListingTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Configuration
public class ListingTemplateMappersConfig {

    private final MapperLookupService mapperLookupService;

    public ListingTemplateMappersConfig(MapperLookupService mapperLookupService) {
        this.mapperLookupService = mapperLookupService;
    }

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            .withZone(ZoneOffset.UTC);


    @Bean("templateDtoMapper")
    public ModelMapper templateDtoMapper() {

        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mm.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

        mm.createTypeMap(ListingTemplate.class, ListingTemplateResponseDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        Users user = (Users) ctx.getSource();
                        return user != null ? user.getUserId() : null;
                    }).map(ListingTemplate::getUser, ListingTemplateResponseDto::setUserId);

                    mapper.using(ctx -> {
                        ServerRegion server = (ServerRegion) ctx.getSource();
                        return server != null ? server.getServerName() : null;
                    }).map(ListingTemplate::getServer, ListingTemplateResponseDto::setServer);

                    mapper.using(ctx -> {
                        ServerRegion server = (ServerRegion) ctx.getSource();
                        return server != null ? server.getServerId() : null;
                    }).map(ListingTemplate::getServer, ListingTemplateResponseDto::setServerId);

                    mapper.using(ctx -> {
                        GameEnvironment environment = (GameEnvironment) ctx.getSource();
                        return environment != null ? environment.getEnvironmentType() : null;
                    }).map(ListingTemplate::getEnvironment, ListingTemplateResponseDto::setEnvironment);

                    mapper.using(ctx -> {
                        GameEnvironment environment = (GameEnvironment) ctx.getSource();
                        return environment != null ? environment.getEnvironmentId() : null;
                    }).map(ListingTemplate::getEnvironment, ListingTemplateResponseDto::setEnvironmentId);

                    mapper.using(ctx -> {
                        GameExperience experience = (GameExperience) ctx.getSource();
                        return experience != null ? experience.getExperienceType() : null;
                    }).map(ListingTemplate::getExperience, ListingTemplateResponseDto::setExperience);

                    mapper.using(ctx -> {
                        GameExperience experience = (GameExperience) ctx.getSource();
                        return experience != null ? experience.getExperienceId() : null;
                    }).map(ListingTemplate::getExperience, ListingTemplateResponseDto::setExperienceId);

                    mapper.map(ListingTemplate::getListingTitle, ListingTemplateResponseDto::setListingTitle);

//checking nullable field for null to convert to an empty string if it is null
                    mapper.using(ctx -> {
                        PlayStyle playStyle = (PlayStyle) ctx.getSource();
                        if (playStyle == null) {
                            return "";
                        }
                        else {
                            return playStyle.getPlayStyle();
                        }
                    }).map(ListingTemplate::getPlayStyle, ListingTemplateResponseDto::setPlayStyle);

                    mapper.using(ctx -> {
                        PlayStyle style = (PlayStyle) ctx.getSource();
                        if(style == null) {
                            return null;
                        }
                        else {
                            return style.getStyleId();
                        }
                    }).map(ListingTemplate::getPlayStyle, ListingTemplateResponseDto::setStyleId);

                    mapper.using(ctx -> {
                        Legality legality = (Legality) ctx.getSource();
                        return legality != null ? legality.getLegalityStatus() : null;
                    }).map(ListingTemplate::getLegality, ListingTemplateResponseDto::setLegality);

                    mapper.using(ctx -> {
                        Legality legality = (Legality) ctx.getSource();
                        return legality != null ? legality.getLegalityId() : null;
                    }).map(ListingTemplate::getLegality, ListingTemplateResponseDto::setLegalityId);

                    mapper.using(ctx -> {
                        GroupStatus groupStatus = (GroupStatus) ctx.getSource();
                        return groupStatus != null ? groupStatus.getGroupStatus() : null;
                    }).map(ListingTemplate::getGroupStatus, ListingTemplateResponseDto::setGroupStatus);

                    mapper.using(ctx -> {
                        GroupStatus groupStatus = (GroupStatus) ctx.getSource();
                        return groupStatus != null ? groupStatus.getGroupStatusId() : null;
                    }).map(ListingTemplate::getGroupStatus, ListingTemplateResponseDto::setGroupStatusId);

                    mapper.map(ListingTemplate::getEventSchedule, ListingTemplateResponseDto::setEventSchedule);

                    mapper.using(ctx -> {
                        GameplayCategory category = (GameplayCategory) ctx.getSource();
                        return category != null ? category.getCategoryName() : null;
                    }).map(ListingTemplate::getCategory, ListingTemplateResponseDto::setCategory);

                    mapper.using(ctx -> {
                        GameplayCategory cat = (GameplayCategory) ctx.getSource();
                        return cat != null ? cat.getCategoryId() : null;
                    }).map(ListingTemplate::getCategory, ListingTemplateResponseDto::setCategoryId);

                    mapper.using(ctx -> {
                        GameplaySubcategory subcategory = (GameplaySubcategory) ctx.getSource();
                        if(subcategory == null) {
                            return "";
                        }
                        else {
                            return subcategory.getSubcategoryName();
                        }
                    }).map(ListingTemplate::getSubcategory, ListingTemplateResponseDto::setSubcategory);

                    mapper.using(ctx -> {
                        GameplaySubcategory subcat = (GameplaySubcategory) ctx.getSource();
                        if(subcat == null) {
                            return null;
                        }
                        else {
                            return subcat.getSubcategoryId();
                        }
                    }).map(ListingTemplate::getSubcategory, ListingTemplateResponseDto::setSubcategoryId);

                    mapper.using(ctx -> {
                        PvpStatus pvpStatus = (PvpStatus) ctx.getSource();
                        return pvpStatus != null ? pvpStatus.getPvpStatus() : null;
                    }).map(ListingTemplate::getPvpStatus, ListingTemplateResponseDto::setPvpStatus);

                    mapper.using(ctx -> {
                        PvpStatus pvp = (PvpStatus) ctx.getSource();
                        return pvp != null ? pvp.getPvpStatusId() : null;
                    }).map(ListingTemplate::getPvpStatus, ListingTemplateResponseDto::setPvpStatusId);

                    mapper.using(ctx -> {
                        PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                        return system != null ? system.getSystemName() : null;
                    }).map(ListingTemplate::getSystem, ListingTemplateResponseDto::setSystem);

                    mapper.using(ctx -> {
                        PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                        return system != null ? system.getSystemId() : null;
                    }).map(ListingTemplate::getSystem, ListingTemplateResponseDto::setSystemId);

                    mapper.using(ctx -> {
                        PlanetMoonSystem planet = (PlanetMoonSystem) ctx.getSource();
                        if (planet == null) {
                            return "";
                        }
                        else {
                            return planet.getPlanetName();
                        }
                    }).map(ListingTemplate::getPlanetMoonSystem, ListingTemplateResponseDto::setPlanetMoonSystem);

                    mapper.using(ctx -> {
                        PlanetMoonSystem planet = (PlanetMoonSystem) ctx.getSource();
                        if(planet == null) {
                            return null;
                        }
                        else {
                            return planet.getPlanetId();
                        }
                    }).map(ListingTemplate::getPlanetMoonSystem, ListingTemplateResponseDto::setPlanetId);

                    mapper.using(ctx -> {
                        String commsService = (String) ctx.getSource();
                        return Objects.requireNonNullElse(commsService, "");
                    }).map(ListingTemplate::getCommsService, ListingTemplateResponseDto::setCommsService);
                });

        return mm;
    }

    @Bean("templateEntityMapper")
    public ModelMapper templateEntityMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<CreateGroupListingDto, Instant> dateTimeAndZoneToInstantConverter = ctx -> {
            CreateGroupListingDto src = ctx.getSource();
            if (src == null) return null;

            String dateStr = src.getEventDate();
            String timeStr = src.getEventTime();
            String zoneStr = src.getEventTimeZone();

            if (dateStr == null || timeStr == null || zoneStr == null) return null;
            if (dateStr.isBlank() || timeStr.isBlank() || zoneStr.isBlank()) return null;

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            ZoneId zone = ZoneId.of(zoneStr);

            return ZonedDateTime.of(date, time, zone).toInstant();
        };

        modelMapper.createTypeMap(CreateGroupListingDto.class, ListingTemplate.class)
                .addMappings(mapper -> {

                    //skipping auto-generated fields
                    mapper.skip(ListingTemplate::setTemplateId);
                    mapper.skip(ListingTemplate::setCreationTimestamp);

                    //userId to user entity
                    mapper.using((MappingContext<Long, Users> ctx) -> mapperLookupService.findUserById(ctx.getSource()))
                            .map(CreateGroupListingDto::getUserId, ListingTemplate::setUser);

                    //serverId to server entity
                    mapper.using((MappingContext<Integer, ServerRegion> ctx) -> mapperLookupService.findServerRegionById(ctx.getSource()))
                            .map(CreateGroupListingDto::getServerId, ListingTemplate::setServer);

                    //environmentId to environment entity
                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) -> mapperLookupService.findEnvironmentById(ctx.getSource()))
                            .map(CreateGroupListingDto::getEnvironmentId, ListingTemplate::setEnvironment);

                    //experienceId to experience entity
                    mapper.using((MappingContext<Integer, GameExperience> ctx) -> mapperLookupService.findExperienceById(ctx.getSource()))
                            .map(CreateGroupListingDto::getExperienceId, ListingTemplate::setExperience);

                    //listing title mapped automatically by model mapper due to property name and type match

                    //playStyleId to playStyle entity
                    mapper.using((MappingContext<Integer, PlayStyle> ctx) -> {
                        Integer playStyleId = ctx.getSource();
                        if (playStyleId == null) {
                            return null;
                        }
                        return mapperLookupService.findPlayStyleById(ctx.getSource());
                    }).map(CreateGroupListingDto::getPlayStyleId, ListingTemplate::setPlayStyle);

                    //legalityId to legality entity
                    mapper.using((MappingContext<Integer, Legality> ctx) -> mapperLookupService.findLegalityById(ctx.getSource()))
                            .map(CreateGroupListingDto::getLegalityId, ListingTemplate::setLegality);

                    //groupStatusId to groupStatus entity
                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mapperLookupService.findGroupStatusById(ctx.getSource()))
                            .map(CreateGroupListingDto::getGroupStatusId, ListingTemplate::setGroupStatus);

                    //eventScheduleDate mapped to eventSchedule Instant
                    mapper.using(dateTimeAndZoneToInstantConverter).map(src -> src, ListingTemplate::setEventSchedule);

                    //categoryId to category entity
                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mapperLookupService.findCategoryById(ctx.getSource()))
                            .map(CreateGroupListingDto::getCategoryId, ListingTemplate::setCategory);

                    //subcategoryId to subcategory entity
                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> {
                        Integer subcategoryId = ctx.getSource();
                        if (subcategoryId == null) {
                            return null;
                        }
                        return mapperLookupService.findSubcategoryById(ctx.getSource());
                    }).map(CreateGroupListingDto::getSubcategoryId, ListingTemplate::setSubcategory);

                    //pvpStatusId to pvp status entity
                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mapperLookupService.findPvpStatusById(ctx.getSource()))
                            .map(CreateGroupListingDto::getPvpStatusId, ListingTemplate::setPvpStatus);

                    //systemId to planetary system entity
                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mapperLookupService.findPlanetarySystemById(ctx.getSource()))
                            .map(CreateGroupListingDto::getSystemId, ListingTemplate::setSystem);

                    //planetId to planet moon system entity
                    mapper.using((MappingContext<Integer, PlanetMoonSystem> ctx) -> {
                        Integer planetId = ctx.getSource();
                        if (planetId == null) {
                            return null;
                        }

                        return mapperLookupService.findPlanetMoonSystemById(planetId);
                    }).map(CreateGroupListingDto::getPlanetId, ListingTemplate::setPlanetMoonSystem);

                });
        return modelMapper;
    }
}
