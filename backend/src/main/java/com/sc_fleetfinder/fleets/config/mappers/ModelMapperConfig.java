package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupRankAssignedPrivilegeRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrEditListingTemplateDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreateOrEditCustomNotificationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.*;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.*;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetCustomNotificationResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.entities.*;
import com.sc_fleetfinder.fleets.entities.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.*;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import com.sc_fleetfinder.fleets.utils.MessageType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            .withZone(ZoneOffset.UTC);

    private final MapperLookupService mls;

    Converter<CreateGroupListingDto, Instant> dateTimeAndZoneToInstantConverter = ctx -> {
        CreateGroupListingDto src = ctx.getSource();
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

    Converter<UpdateGroupListingDto, Instant> UpdateListingDateTimeZoneToInstantConverter = ctx -> {
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

    Converter<CreateOrEditListingTemplateDto, Instant> templateDateTimeToInstantConverter = ctx -> {
        CreateOrEditListingTemplateDto src = ctx.getSource();
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


    @Bean
    public ModelMapper fleetFinderModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

//USERS
        // Users ---->>>> PRIVATE user response dto
        modelMapper.createTypeMap(Users.class, PrivateUserResponseDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        ServerRegion serverRegion = (ServerRegion) ctx.getSource();
                        return serverRegion != null ? serverRegion.getServerName() : null;
                    }).map(Users::getServer, PrivateUserResponseDto::setServer);
                });

//GROUP LISTINGS
        // Group Listing ---->>> Response Dto
        modelMapper.createTypeMap(GroupListing.class, GroupListingResponseDto.class)
                .addMappings(mapper -> {

                    mapper.map(GroupListing::getGroupId, GroupListingResponseDto::setGroupId);

                    mapper.using(ctx -> {
                        Users user = (Users) ctx.getSource();
                        return user != null ? user.getUserId() : null;
                    }).map(GroupListing::getUsers, GroupListingResponseDto::setUserId);

                    mapper.using(ctx -> {
                        Users users = (Users) ctx.getSource();
                        return users != null ? users.getUsername() : null;
                    }).map(GroupListing::getUsers, GroupListingResponseDto::setUserName);

                    mapper.using(ctx -> {
                        ServerRegion server = (ServerRegion) ctx.getSource();
                        return server != null ? server.getServerName() : null;
                    }).map(GroupListing::getServer, GroupListingResponseDto::setServer);

                    mapper.using(ctx -> {
                        ServerRegion server = (ServerRegion) ctx.getSource();
                        return server != null ? server.getServerId() : null;
                    }).map(GroupListing::getServer, GroupListingResponseDto::setServerId);

                    mapper.using(ctx -> {
                        GameEnvironment environment = (GameEnvironment) ctx.getSource();
                        return environment != null ? environment.getEnvironmentType() : null;
                    }).map(GroupListing::getEnvironment, GroupListingResponseDto::setEnvironment);

                    mapper.using(ctx -> {
                        GameEnvironment environment = (GameEnvironment) ctx.getSource();
                        return environment != null ? environment.getEnvironmentId() : null;
                    }).map(GroupListing::getEnvironment, GroupListingResponseDto::setEnvironmentId);

                    mapper.using(ctx -> {
                        GameExperience experience = (GameExperience) ctx.getSource();
                        return experience != null ? experience.getExperienceType() : null;
                    }).map(GroupListing::getExperience, GroupListingResponseDto::setExperience);

                    mapper.using(ctx -> {
                        GameExperience experience = (GameExperience) ctx.getSource();
                        return experience != null ? experience.getExperienceId() : null;
                    }).map(GroupListing::getExperience, GroupListingResponseDto::setExperienceId);

                    mapper.map(GroupListing::getListingTitle, GroupListingResponseDto::setListingTitle);

                    //checking nullable field for null to convert to an empty string if it is null
                    mapper.using(ctx -> {
                        PlayStyle playStyle = (PlayStyle) ctx.getSource();
                        if (playStyle == null) {
                            return "";
                        }
                        else {
                            return playStyle.getPlayStyle();
                        }
                    }).map(GroupListing::getPlayStyle, GroupListingResponseDto::setPlayStyle);

                    mapper.using(ctx -> {
                        PlayStyle style = (PlayStyle) ctx.getSource();
                        if(style == null) {
                            return null;
                        }
                        else {
                            return style.getStyleId();
                        }
                    }).map(GroupListing::getPlayStyle, GroupListingResponseDto::setStyleId);

                    mapper.using(ctx -> {
                        Legality legality = (Legality) ctx.getSource();
                        return legality != null ? legality.getLegalityStatus() : null;
                    }).map(GroupListing::getLegality, GroupListingResponseDto::setLegality);

                    mapper.using(ctx -> {
                        Legality legality = (Legality) ctx.getSource();
                        return legality != null ? legality.getLegalityId() : null;
                    }).map(GroupListing::getLegality, GroupListingResponseDto::setLegalityId);

                    mapper.using(ctx -> {
                        GroupStatus groupStatus = (GroupStatus) ctx.getSource();
                        return groupStatus != null ? groupStatus.getGroupStatus() : null;
                    }).map(GroupListing::getGroupStatus, GroupListingResponseDto::setGroupStatus);

                    mapper.using(ctx -> {
                        GroupStatus groupStatus = (GroupStatus) ctx.getSource();
                        return groupStatus != null ? groupStatus.getGroupStatusId() : null;
                    }).map(GroupListing::getGroupStatus, GroupListingResponseDto::setGroupStatusId);

                    mapper.map(GroupListing::getEventSchedule, GroupListingResponseDto::setEventSchedule);

                    mapper.using(ctx -> {
                        GameplayCategory category = (GameplayCategory) ctx.getSource();
                        return category != null ? category.getCategoryName() : null;
                    }).map(GroupListing::getCategory, GroupListingResponseDto::setCategory);

                    mapper.using(ctx -> {
                        GameplayCategory cat = (GameplayCategory) ctx.getSource();
                        return cat != null ? cat.getCategoryId() : null;
                    }).map(GroupListing::getCategory, GroupListingResponseDto::setCategoryId);

                    mapper.using(ctx -> {
                        GameplaySubcategory subcategory = (GameplaySubcategory) ctx.getSource();
                        if(subcategory == null) {
                            return "";
                        }
                        else {
                            return subcategory.getSubcategoryName();
                        }
                    }).map(GroupListing::getSubcategory, GroupListingResponseDto::setSubcategory);

                    mapper.using(ctx -> {
                        GameplaySubcategory subcat = (GameplaySubcategory) ctx.getSource();
                        if(subcat == null) {
                            return null;
                        }
                        else {
                            return subcat.getSubcategoryId();
                        }
                    }).map(GroupListing::getSubcategory, GroupListingResponseDto::setSubcategoryId);

                    mapper.using(ctx -> {
                        PvpStatus pvpStatus = (PvpStatus) ctx.getSource();
                        return pvpStatus != null ? pvpStatus.getPvpStatus() : null;
                    }).map(GroupListing::getPvpStatus, GroupListingResponseDto::setPvpStatus);

                    mapper.using(ctx -> {
                        PvpStatus pvp = (PvpStatus) ctx.getSource();
                        return pvp != null ? pvp.getPvpStatusId() : null;
                    }).map(GroupListing::getPvpStatus, GroupListingResponseDto::setPvpStatusId);

                    mapper.using(ctx -> {
                        PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                        return system != null ? system.getSystemName() : null;
                    }).map(GroupListing::getSystem, GroupListingResponseDto::setSystem);

                    mapper.using(ctx -> {
                        PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                        return system != null ? system.getSystemId() : null;
                    }).map(GroupListing::getSystem, GroupListingResponseDto::setSystemId);

                    mapper.using(ctx -> {
                        PlanetMoonSystem planet = (PlanetMoonSystem) ctx.getSource();
                        if (planet == null) {
                            return "";
                        }
                        else {
                            return planet.getPlanetName();
                        }
                    }).map(GroupListing::getPlanetMoonSystem, GroupListingResponseDto::setPlanetMoonSystem);

                    mapper.using(ctx -> {
                        PlanetMoonSystem planet = (PlanetMoonSystem) ctx.getSource();
                        if(planet == null) {
                            return null;
                        }
                        else {
                            return planet.getPlanetId();
                        }
                    }).map(GroupListing::getPlanetMoonSystem, GroupListingResponseDto::setPlanetId);

                    mapper.map(GroupListing::getListingDescription, GroupListingResponseDto::setListingDescription);

                    mapper.map(GroupListing::getDesiredPartySize, GroupListingResponseDto::setDesiredPartySize);

                    mapper.map(GroupListing::getCurrentPartySize, GroupListingResponseDto::setCurrentPartySize);

                    mapper.using(ctx -> {
                        String availableRoles = (String) ctx.getSource();
                        return Objects.requireNonNullElse(availableRoles, "");
                    }).map(GroupListing::getAvailableRoles, GroupListingResponseDto::setAvailableRoles);

                    mapper.map(GroupListing::getCommsOption, GroupListingResponseDto::setCommsOption);

                    mapper.using(ctx -> {
                        String commsService = (String) ctx.getSource();
                        return Objects.requireNonNullElse(commsService, "");
                    }).map(GroupListing::getCommsService, GroupListingResponseDto::setCommsService);

                    mapper.map(GroupListing::getLanguageCode, GroupListingResponseDto::setLanguageCode);

                    mapper.map(GroupListing::getCreationTimestamp, GroupListingResponseDto::setCreationTimestamp);

                    mapper.map(GroupListing::getLastUpdated, GroupListingResponseDto::setLastUpdated);
                });

        // Create Group Listing ---->>>>  entity
        modelMapper.createTypeMap(CreateGroupListingDto.class, GroupListing.class)
                .addMappings(mapper -> {

                    //skipping auto-generated fields
                    mapper.skip(GroupListing::setGroupId);
                    mapper.skip(GroupListing::setCreationTimestamp);
                    mapper.skip(GroupListing::setLastUpdated);

                    //userId to user entity
                    mapper.using((MappingContext<Long, Users> ctx) -> mls.findUserById(ctx.getSource()))
                            .map(CreateGroupListingDto::getUserId, GroupListing::setUsers);

                    //serverId to server entity
                    mapper.using((MappingContext<Integer, ServerRegion> ctx) -> mls.findServerRegionById(ctx.getSource()))
                            .map(CreateGroupListingDto::getServerId, GroupListing::setServer);

                    //environmentId to environment entity
                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) -> mls.findEnvironmentById(ctx.getSource()))
                            .map(CreateGroupListingDto::getEnvironmentId, GroupListing::setEnvironment);

                    //experienceId to experience entity
                    mapper.using((MappingContext<Integer, GameExperience> ctx) -> mls.findExperienceById(ctx.getSource()))
                            .map(CreateGroupListingDto::getExperienceId, GroupListing::setExperience);

                    //listing title mapped automatically by model mapper due to property name and type match

                    //playStyleId to playStyle entity
                    mapper.using((MappingContext<Integer, PlayStyle> ctx) -> {
                        Integer playStyleId = ctx.getSource();
                        if (playStyleId == null) {
                            return null;
                        }
                        return mls.findPlayStyleById(ctx.getSource());
                    }).map(CreateGroupListingDto::getPlayStyleId, GroupListing::setPlayStyle);

                    //legalityId to legality entity
                    mapper.using((MappingContext<Integer, Legality> ctx) -> mls.findLegalityById(ctx.getSource()))
                            .map(CreateGroupListingDto::getLegalityId, GroupListing::setLegality);

                    //groupStatusId to groupStatus entity
                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mls.findGroupStatusById(ctx.getSource()))
                            .map(CreateGroupListingDto::getGroupStatusId, GroupListing::setGroupStatus);

                    //eventScheduleDate mapped to eventSchedule Instant
                    mapper.using(dateTimeAndZoneToInstantConverter).map(src -> src, GroupListing::setEventSchedule);

                    //categoryId to category entity
                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mls.findCategoryById(ctx.getSource()))
                            .map(CreateGroupListingDto::getCategoryId, GroupListing::setCategory);

                    //subcategoryId to subcategory entity
                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> {
                        Integer subcategoryId = ctx.getSource();
                        if (subcategoryId == null) {
                            return null;
                        }
                        return mls.findSubcategoryById(ctx.getSource());
                    }).map(CreateGroupListingDto::getSubcategoryId, GroupListing::setSubcategory);

                    //pvpStatusId to pvp status entity
                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mls.findPvpStatusById(ctx.getSource()))
                            .map(CreateGroupListingDto::getPvpStatusId, GroupListing::setPvpStatus);

                    //systemId to planetary system entity
                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mls.findPlanetarySystemById(ctx.getSource()))
                            .map(CreateGroupListingDto::getSystemId, GroupListing::setSystem);

                    //planetId to planet moon system entity
                    mapper.using((MappingContext<Integer, PlanetMoonSystem> ctx) -> {
                        Integer planetId = ctx.getSource();
                        if (planetId == null) {
                            return null;
                        }

                        return mls.findPlanetMoonSystemById(planetId);
                    }).map(CreateGroupListingDto::getPlanetId, GroupListing::setPlanetMoonSystem);

                    //listing description mapped automatically due to property name and type match

                    //desired party size mapped automatically due to property name and type match

                    //current party size mapped automatically due to property name and type match

                    //available roles mapped automatically due to property name and type match

                    //comms option mapped automatically due to property name and type match

                    //comms service mapped automatically due to property name and type match

                    //language code mapped automatically due to property name and type match

                });

        // Update Group Listing ---->>>> entity
        modelMapper.createTypeMap(UpdateGroupListingDto.class, GroupListing.class)
                .addMappings(mapper -> {

                    //skipping fixed fields
                    mapper.skip(GroupListing::setGroupId);
                    mapper.skip(GroupListing::setCreationTimestamp);
                    mapper.skip(GroupListing::setUsers);

                    mapper.using((MappingContext<Integer, ServerRegion> ctx) ->
                                    mls.findServerRegionById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getServerId, GroupListing::setServer);

                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) ->
                                    mls.findEnvironmentById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getEnvironmentId, GroupListing::setEnvironment);

                    mapper.using((MappingContext<Integer, GameExperience> ctx) ->
                                    mls.findExperienceById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getExperienceId, GroupListing::setExperience);

                    //listing title mapped automatically by model mapper due to property name and type match

                    //playStyleId to playStyle entity
                    mapper.using((MappingContext<Integer, PlayStyle> ctx) -> {
                        Integer playStyleId = ctx.getSource();
                        if (playStyleId == null) {
                            return null;
                        }
                        return mls.findPlayStyleById(ctx.getSource());
                    }).map(UpdateGroupListingDto::getPlayStyleId, GroupListing::setPlayStyle);

                    //legalityId to legality entity
                    mapper.using((MappingContext<Integer, Legality> ctx) -> mls.findLegalityById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getLegalityId, GroupListing::setLegality);

                    //groupStatusId to groupStatus entity
                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mls.findGroupStatusById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getGroupStatusId, GroupListing::setGroupStatus);

                    //eventScheduleDate mapped to eventSchedule Instant
                    mapper.using(UpdateListingDateTimeZoneToInstantConverter).map(src -> src, GroupListing::setEventSchedule);

                    //categoryId to category entity
                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mls.findCategoryById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getCategoryId, GroupListing::setCategory);

                    //subcategoryId to subcategory entity
                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> {
                        Integer subcategoryId = ctx.getSource();
                        if (subcategoryId == null) {
                            return null;
                        }
                        return mls.findSubcategoryById(ctx.getSource());
                    }).map(UpdateGroupListingDto::getSubcategoryId, GroupListing::setSubcategory);

                    //pvpStatusId to pvp status entity
                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mls.findPvpStatusById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getPvpStatusId, GroupListing::setPvpStatus);

                    //systemId to planetary system entity
                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mls.findPlanetarySystemById(ctx.getSource()))
                            .map(UpdateGroupListingDto::getSystemId, GroupListing::setSystem);

                    //planetId to planet moon system entity
                    mapper.using((MappingContext<Integer, PlanetMoonSystem> ctx) -> {
                        Integer planetId = ctx.getSource();
                        if (planetId == null) {
                            return null;
                        }

                        return mls.findPlanetMoonSystemById(planetId);
                    }).map(UpdateGroupListingDto::getPlanetId, GroupListing::setPlanetMoonSystem);

                    //listing description mapped automatically due to property name and type match

                    //desired party size mapped automatically due to property name and type match

                    //current party size mapped automatically due to property name and type match

                    //available roles mapped automatically due to property name and type match

                    //comms option mapped automatically due to property name and type match

                    //comms service mapped automatically due to property name and type match

                    //language code mapped automatically due to property name and type match
                });

//LISTING TEMPLATES
        // Listing Template --->>> Response Dto
        modelMapper.createTypeMap(ListingTemplate.class, ListingTemplateResponseDto.class)
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

                    mapper.map(ListingTemplate::getLanguageCode, ListingTemplateResponseDto::setLanguageCode);
                });

        // Create/Edit Listing Template ---->>> entity
        modelMapper.createTypeMap(CreateOrEditListingTemplateDto.class, ListingTemplate.class)
                .addMappings(mapper -> {

                    mapper.skip(ListingTemplate::setTemplateId);
                    mapper.skip(ListingTemplate::setCreationTimestamp);

                    //userId to user entity
                    mapper.using((MappingContext<Long, Users> ctx) -> mls.findUserById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getUserId, ListingTemplate::setUser);

                    //serverId to server entity
                    mapper.using((MappingContext<Integer, ServerRegion> ctx) -> mls.findServerRegionById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getServerId, ListingTemplate::setServer);

                    //environmentId to environment entity
                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) -> mls.findEnvironmentById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getEnvironmentId, ListingTemplate::setEnvironment);

                    //experienceId to experience entity
                    mapper.using((MappingContext<Integer, GameExperience> ctx) -> mls.findExperienceById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getExperienceId, ListingTemplate::setExperience);

                    //listing title mapped automatically by model mapper due to property name and type match

                    //playStyleId to playStyle entity
                    mapper.using((MappingContext<Integer, PlayStyle> ctx) -> {
                        Integer playStyleId = ctx.getSource();
                        if (playStyleId == null) {
                            return null;
                        }
                        return mls.findPlayStyleById(ctx.getSource());
                    }).map(CreateOrEditListingTemplateDto::getPlayStyleId, ListingTemplate::setPlayStyle);

                    //legalityId to legality entity
                    mapper.using((MappingContext<Integer, Legality> ctx) -> mls.findLegalityById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getLegalityId, ListingTemplate::setLegality);

                    //groupStatusId to groupStatus entity
                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mls.findGroupStatusById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getGroupStatusId, ListingTemplate::setGroupStatus);

                    //eventScheduleDate mapped to eventSchedule Instant
                    mapper.using(templateDateTimeToInstantConverter).map(src -> src, ListingTemplate::setEventSchedule);

                    //categoryId to category entity
                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mls.findCategoryById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getCategoryId, ListingTemplate::setCategory);

                    //subcategoryId to subcategory entity
                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> {
                        Integer subcategoryId = ctx.getSource();
                        if (subcategoryId == null) {
                            return null;
                        }
                        return mls.findSubcategoryById(ctx.getSource());
                    }).map(CreateOrEditListingTemplateDto::getSubcategoryId, ListingTemplate::setSubcategory);

                    //pvpStatusId to pvp status entity
                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mls.findPvpStatusById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getPvpStatusId, ListingTemplate::setPvpStatus);

                    //systemId to planetary system entity
                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mls.findPlanetarySystemById(ctx.getSource()))
                            .map(CreateOrEditListingTemplateDto::getSystemId, ListingTemplate::setSystem);

                    //planetId to planet moon system entity
                    mapper.using((MappingContext<Integer, PlanetMoonSystem> ctx) -> {
                        Integer planetId = ctx.getSource();
                        if (planetId == null) {
                            return null;
                        }

                        return mls.findPlanetMoonSystemById(planetId);
                    }).map(CreateOrEditListingTemplateDto::getPlanetId, ListingTemplate::setPlanetMoonSystem);

                    //listing description mapped automatically due to property name and type match

                    //desired party size mapped automatically due to property name and type match

                    //current party size mapped automatically due to property name and type match

                    //available roles mapped automatically due to property name and type match

                    //comsms option mapped automatically due to property name and type match

                    //comsms service mapped automatically due to property name and type match

                    //language code mapped automatically due to property name and type match

                });

//MODERATION
        // Mod Listing Action ---->>>> Response Dto
        modelMapper.createTypeMap(ModListingAction.class, ModListingActionDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        ListingArchive archive = (ListingArchive) ctx.getSource();
                        return archive != null ? archive.getArchiveId() : null;
                    }).map(ModListingAction::getArchive, ModListingActionDto::setArchiveId);
                });

        // Mod Issue ----->>>>>> Response Dto
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

// NOTIFICATIONS
        modelMapper.createTypeMap(Notification.class, GetNotificationDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        NotificationOutbox ob = (NotificationOutbox) ctx.getSource();
                        return ob != null ? ob.getEntityNewStatus() : null;
                    }).map(Notification::getOutbox, GetNotificationDto::setEntityNewStatus);
                });

// CUSTOM NOTIFICATIONS
        // Create/Edit Custom Notification ----->>>> entity
        modelMapper.createTypeMap(CreateOrEditCustomNotificationDto.class, UserCustomNotification.class)
                .addMappings(mapper -> {

                    mapper.skip(UserCustomNotification::setCustomNoteId);
                    mapper.skip(UserCustomNotification::setUser);
                    mapper.skip(UserCustomNotification::setEnabled);
                    mapper.skip(UserCustomNotification::setCreatedAt);

                    mapper.map(CreateOrEditCustomNotificationDto::getTagLabel, UserCustomNotification::setTagLabel);

                    mapper.using((MappingContext<Integer, ServerRegion> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findServerRegionById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getServerId, UserCustomNotification::setServer);

                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findEnvironmentById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getEnvironmentId, UserCustomNotification::setEnvironment);

                    mapper.using((MappingContext<Integer, GameExperience> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findExperienceById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getExperienceId, UserCustomNotification::setExperience);

                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findCategoryById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getCategoryId, UserCustomNotification::setCategory);

                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findSubcategoryById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getSubcategoryId, UserCustomNotification::setSubcategory);

                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findPlanetarySystemById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getSystemId, UserCustomNotification::setSystem);

                    mapper.map(CreateOrEditCustomNotificationDto::getLanguageCode, UserCustomNotification::setLanguageCode);

                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findPvpStatusById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getPvpStatusId, UserCustomNotification::setPvpStatus);

                    mapper.using((MappingContext<Integer, Legality> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findLegalityById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getLegalityId, UserCustomNotification::setLegality);

                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> {
                        Integer id = ctx.getSource();
                        return id != null ? mls.findGroupStatusById(id) : null;
                    }).map(CreateOrEditCustomNotificationDto::getGroupStatusId, UserCustomNotification::setGroupStatus);

                    mapper.map(CreateOrEditCustomNotificationDto::getKeywords, UserCustomNotification::setKeywords);
                });

        // Custom Notification ---->>>>> Response Dto
        modelMapper.createTypeMap(UserCustomNotification.class, GetCustomNotificationResponseDto.class).addMappings(mapper -> {

            mapper.map(UserCustomNotification::getCustomNoteId, GetCustomNotificationResponseDto::setCustomNoteId);

            mapper.map(UserCustomNotification::getEnabled, GetCustomNotificationResponseDto::setEnabled);

            mapper.map(UserCustomNotification::getTagLabel, GetCustomNotificationResponseDto::setTagLabel);

            mapper.using(ctx -> {
                ServerRegion server = (ServerRegion) ctx.getSource();
                return server != null ? server.getServerId() : null;
            }).map(UserCustomNotification::getServer, GetCustomNotificationResponseDto::setServerId);

            mapper.using(ctx -> {
                ServerRegion server = (ServerRegion) ctx.getSource();
                return server != null ? server.getServerName() : null;
            }).map(UserCustomNotification::getServer, GetCustomNotificationResponseDto::setServer);

            mapper.using(ctx -> {
                GameEnvironment environment = (GameEnvironment) ctx.getSource();
                return environment != null ? environment.getEnvironmentId() : null;
            }).map(UserCustomNotification::getEnvironment, GetCustomNotificationResponseDto::setEnvironmentId);

            mapper.using(ctx -> {
                GameEnvironment environment = (GameEnvironment) ctx.getSource();
                return environment != null ? environment.getEnvironmentType() : null;
            }).map(UserCustomNotification::getEnvironment, GetCustomNotificationResponseDto::setEnvironment);

            mapper.using(ctx -> {
                GameExperience experience = (GameExperience) ctx.getSource();
                return experience != null ? experience.getExperienceId() : null;
            }).map(UserCustomNotification::getExperience, GetCustomNotificationResponseDto::setExperienceId);

            mapper.using(ctx -> {
                GameExperience experience = (GameExperience) ctx.getSource();
                return experience != null ? experience.getExperienceType() : null;
            }).map(UserCustomNotification::getExperience, GetCustomNotificationResponseDto::setExperience);

            mapper.using(ctx -> {
                GameplayCategory cat = (GameplayCategory) ctx.getSource();
                return cat != null ? cat.getCategoryId() : null;
            }).map(UserCustomNotification::getCategory, GetCustomNotificationResponseDto::setCategoryId);

            mapper.using(ctx -> {
                GameplayCategory cat = (GameplayCategory) ctx.getSource();
                return cat != null ? cat.getCategoryName() : null;
            }).map(UserCustomNotification::getCategory, GetCustomNotificationResponseDto::setCategory);

            mapper.using(ctx -> {
                GameplaySubcategory subcat = (GameplaySubcategory) ctx.getSource();
                return subcat != null ? subcat.getSubcategoryId() : null;
            }).map(UserCustomNotification::getSubcategory, GetCustomNotificationResponseDto::setSubcategoryId);

            mapper.using(ctx -> {
                GameplaySubcategory subcat = (GameplaySubcategory) ctx.getSource();
                return subcat != null ? subcat.getSubcategoryName() : null;
            }).map(UserCustomNotification::getSubcategory, GetCustomNotificationResponseDto::setSubcategory);

            mapper.using(ctx -> {
                PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                return system != null ? system.getSystemId() : null;
            }).map(UserCustomNotification::getSystem, GetCustomNotificationResponseDto::setSystemId);

            mapper.using(ctx -> {
                PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                return system != null ? system.getSystemName() : null;
            }).map(UserCustomNotification::getSystem, GetCustomNotificationResponseDto::setSystem);

            mapper.map(UserCustomNotification::getLanguageCode, GetCustomNotificationResponseDto::setLanguageCode);

            mapper.using(ctx -> {
                PvpStatus pvp = (PvpStatus) ctx.getSource();
                return pvp != null ? pvp.getPvpStatusId() : null;
            }).map(UserCustomNotification::getPvpStatus, GetCustomNotificationResponseDto::setPvpStatusId);

            mapper.using(ctx -> {
                PvpStatus pvp = (PvpStatus) ctx.getSource();
                return pvp != null ? pvp.getPvpStatus() : null;
            }).map(UserCustomNotification::getPvpStatus, GetCustomNotificationResponseDto::setPvpStatus);

            mapper.using(ctx -> {
                Legality legality = (Legality) ctx.getSource();
                return legality != null ? legality.getLegalityId() : null;
            }).map(UserCustomNotification::getLegality, GetCustomNotificationResponseDto::setLegalityId);

            mapper.using(ctx -> {
                Legality legality = (Legality) ctx.getSource();
                return legality != null ? legality.getLegalityStatus() : null;
            }).map(UserCustomNotification::getLegality, GetCustomNotificationResponseDto::setLegality);

            mapper.using(ctx -> {
                GroupStatus group = (GroupStatus) ctx.getSource();
                return group != null ? group.getGroupStatusId() : null;
            }).map(UserCustomNotification::getGroupStatus, GetCustomNotificationResponseDto::setGroupStatusId);

            mapper.using(ctx -> {
                GroupStatus group = (GroupStatus) ctx.getSource();
                return group != null ? group.getGroupStatus() : null;
            }).map(UserCustomNotification::getGroupStatus, GetCustomNotificationResponseDto::setGroupStatus);

            mapper.map(UserCustomNotification::getKeywords, GetCustomNotificationResponseDto::setKeywords);

            mapper.map(UserCustomNotification::getCreatedAt, GetCustomNotificationResponseDto::setCreatedAt);
        });

//CHAT MESSAGES
        // Chat Message ---->>>> Response Dto
        modelMapper.createTypeMap(Message.class, GetMessageDto.class)
                .addMappings(mapper -> {

                    mapper.map(Message::getMsgId, GetMessageDto::setMsgId);

                    mapper.using(ctx -> {
                        Conversation conv = (Conversation) ctx.getSource();
                        return conv != null ? conv.getConversationId() : null;
                    }).map(Message::getConversation, GetMessageDto::setConversationId);

                    mapper.using(ctx -> {
                        Users sender = (Users) ctx.getSource();
                        return sender != null ? sender.getUserId() : null;
                    }).map(Message::getSender, GetMessageDto::setSenderId);

                    mapper.using(ctx -> {
                        Users sender = (Users) ctx.getSource();
                        return sender != null ? sender.getUsername() : null;
                    }).map(Message::getSender, GetMessageDto::setSenderName);

                    mapper.using(ctx -> {
                        MessageType type = (MessageType) ctx.getSource();
                        return type != null ? type.toString() : null;
                    }).map(Message::getMessageType, GetMessageDto::setMessageType);

                    mapper.map(Message::getMsgBody, GetMessageDto::setMsgBody);

                    mapper.map(Message::getCreatedAt, GetMessageDto::setCreatedAt);

                    mapper.map(Message::getUpdatedAt, GetMessageDto::setUpdatedAt);

                    mapper.map(Message::getDeletedAt, GetMessageDto::setDeletedAt);

                    mapper.using(ctx -> {
                        Message repliedTo = (Message) ctx.getSource();
                        return repliedTo != null ? repliedTo.getMsgId() : null;
                    }).map(Message::getRepliedToMessage, GetMessageDto::setRepliedToMessageId);

                    mapper.map(Message::getClientMessageId, GetMessageDto::setClientMessageId);
                });

//PUSH SUBSCRIPTIONS
        modelMapper.createTypeMap(PushSubscription.class, GetPushSubDto.class)
                .addMappings(mapper -> {

                    mapper.using(ctx -> {
                        Users user = (Users) ctx.getSource();
                        return user != null ? user.getUserId() : null;
                    }).map(PushSubscription::getUser, GetPushSubDto::setUserId);
                });

//GROUP MANAGEMENT SUBGROUPS
        modelMapper.createTypeMap(GroupManagementSubgroup.class, SubgroupSummaryDto.class)
                .addMappings(mapper -> {
                    mapper.map(GroupManagementSubgroup::getSubgroupId, SubgroupSummaryDto::setSubgroupId);

                    mapper.map(GroupManagementSubgroup::getSubgroupLabel, SubgroupSummaryDto::setSubgroupLabel);

                    mapper.map(GroupManagementSubgroup::getSubgroupNotes, SubgroupSummaryDto::setSubgroupNotes);

                    mapper.using(ctx -> {
                        GroupManagementSubgroup parent = (GroupManagementSubgroup) ctx.getSource();
                        return parent != null ? parent.getSubgroupId() : null;
                    }).map(GroupManagementSubgroup::getParentSubgroup, SubgroupSummaryDto::setParentSubgroupId);

                    mapper.using(ctx -> {
                        GroupManagementSubgroup parent = (GroupManagementSubgroup) ctx.getSource();
                        return parent != null ? parent.getSubgroupLabel() : null;
                    }).map(GroupManagementSubgroup::getParentSubgroup, SubgroupSummaryDto::setParentSubgroupLabel);

                    mapper.map(GroupManagementSubgroup::getIntendedSubgroupSize, SubgroupSummaryDto::setIntendedSubgroupSize);

                    mapper.map(GroupManagementSubgroup::getCreatedAt, SubgroupSummaryDto::setCreatedAt);
                });

//GROUP INVITES
        // Group Invite ---->>>> Response Dto
        modelMapper.createTypeMap(GroupInvite.class, GroupInviteRequestOrResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(GroupInvite::getInviteId, GroupInviteRequestOrResponseDto::setInviteId);

                    mapper.map(GroupInvite::getSender, GroupInviteRequestOrResponseDto::setSenderSummary);

                    mapper.map(GroupInvite::getRecipient, GroupInviteRequestOrResponseDto::setRecipientSummary);

                    mapper.map(GroupInvite::getGroupListing, GroupInviteRequestOrResponseDto::setListingDetails);

                    mapper.map(GroupInvite::getMemberStatus, GroupInviteRequestOrResponseDto::setMemberStatus);

                    mapper.map(GroupInvite::getInviteRole, GroupInviteRequestOrResponseDto::setRoleSummary);

                    mapper.map(GroupInvite::getInviteDirection, GroupInviteRequestOrResponseDto::setInviteDirection);

                    mapper.map(GroupInvite::getInviteStatus, GroupInviteRequestOrResponseDto::setInviteStatus);

                    mapper.map(GroupInvite::getInviteMessage, GroupInviteRequestOrResponseDto::setInviteMessage);

                    mapper.map(GroupInvite::getExpiresAt, GroupInviteRequestOrResponseDto::setExpiresAt);

                    mapper.map(GroupInvite::getCreatedAt, GroupInviteRequestOrResponseDto::setSentAt);
                });

//GROUP MEMBERS
        // Group Member ---->>> Group MemberSHIP Response Dto
        //have to declare an empty type map because it is auto-mapping fields i dont want
        TypeMap<GroupMember, GroupMembershipResponseDto> memberToResponseDtoTypeMap =
                modelMapper.emptyTypeMap(GroupMember.class, GroupMembershipResponseDto.class);

        memberToResponseDtoTypeMap.addMappings(mapper -> {
                    mapper.map(GroupMember::getUser, GroupMembershipResponseDto::setUserSummary);

                    mapper.map(GroupMember::getMemberStatus, GroupMembershipResponseDto::setMemberStatus);

                    //this needs to be handled in the method that calls the transformation
                    mapper.skip(GroupMembershipResponseDto::setMemberRole);

                    mapper.map(GroupMember::getMemberRank, GroupMembershipResponseDto::setMemberRank);

                    mapper.map(GroupMember::getMemberNote, GroupMembershipResponseDto::setMemberNote);

                    mapper.map(GroupMember::getHasComms, GroupMembershipResponseDto::setHasComms);

                    mapper.map(GroupMember::getHasExtNotes, GroupMembershipResponseDto::setHasExtNotes);

                    mapper.map(GroupMember::getRsvpStatus, GroupMembershipResponseDto::setRsvpStatus);

                    mapper.map(GroupMember::getCreatedAt, GroupMembershipResponseDto::setJoinedAt);

                    mapper.skip(GroupMembershipResponseDto::setIsAuthorizedManager);
//                    mapper.using(ctx -> {
//                        InGroupRank rank = (InGroupRank) ctx.getSource();
//                        return rank != null ? hasGroupManagementPrivileges(rank) : null;
//                    }).map(GroupMember::getMemberRank, GroupMembershipResponseDto::setIsAuthorizedManager);

                    mapper.map(GroupMember::getGroupListing, GroupMembershipResponseDto::setListing);
                });

        // Group Member ---->>> Group MANAGER Response DTO
        modelMapper.createTypeMap(GroupMember.class, GroupManagerMemberResponseDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        GroupListing listing = (GroupListing) ctx.getSource();
                        return listing != null ? listing.getGroupId() : null;
                    }).map(GroupMember::getGroupListing, GroupManagerMemberResponseDto::setListingId);

                    mapper.map(GroupMember::getUser, GroupManagerMemberResponseDto::setUserSummary);

                    mapper.map(GroupMember::getMemberStatus, GroupManagerMemberResponseDto::setMemberStatus);

                    mapper.map(GroupMember::getMemberNote, GroupManagerMemberResponseDto::setMemberNote);

                    mapper.map(GroupMember::getHasComms, GroupManagerMemberResponseDto::setHasComms);

                    mapper.map(GroupMember::getHasExtNotes, GroupManagerMemberResponseDto::setHasExtNotes);

                    mapper.map(GroupMember::getRsvpStatus, GroupManagerMemberResponseDto::setRsvpStatus);

                    mapper.map(GroupMember::getCreatedAt, GroupManagerMemberResponseDto::setJoinedAt);

                    mapper.map(GroupMember::getMemberRank, GroupManagerMemberResponseDto::setMemberRank);
                });

//GROUP RANKS
        TypeMap<InGroupRank, GroupRankDto> inGroupRankToDtoTypeMap =
                modelMapper.emptyTypeMap(InGroupRank.class, GroupRankDto.class);

        inGroupRankToDtoTypeMap.addMappings(mapper -> {
                    mapper.map(InGroupRank::getRankId, GroupRankDto::setRankId);

                    mapper.using(ctx -> {
                        GroupListing listing = (GroupListing) ctx.getSource();
                        return listing != null ? listing.getGroupId() : null;
                    }).map(InGroupRank::getGroupListing, GroupRankDto::setListingId);

                    mapper.using(ctx -> {
                        GroupManagementSubgroup scope = (GroupManagementSubgroup) ctx.getSource();
                        return scope != null ? scope.getSubgroupId() : null;
                    }).map(InGroupRank::getRankSubgroupScope, GroupRankDto::setRankSubgroupScope);

                    mapper.map(InGroupRank::getRankTitle, GroupRankDto::setRankTitle);

                    mapper.map(InGroupRank::getRankNotes, GroupRankDto::setRankNotes);

                    mapper.map(InGroupRank::getCreatedByUser, GroupRankDto::setCreatedByUser);

                    mapper.map(InGroupRank::getCreatedAt, GroupRankDto::setCreatedAt);
                });

//CREW POSITIONS
        modelMapper.createTypeMap(CrewPosition.class, MemberPositionSummaryDto.class)
                .addMappings(mapper -> {
                    mapper.map(CrewPosition::getPositionId, MemberPositionSummaryDto::setPositionId);

                    mapper.using(ctx -> {
                        GroupListing listing = (GroupListing) ctx.getSource();
                        return listing != null ? listing.getGroupId() : null;
                    }).map(CrewPosition::getGroupListing, MemberPositionSummaryDto::setListingId);

                    mapper.map(CrewPosition::getSubgroup, MemberPositionSummaryDto::setSubgroupSummary);

                    mapper.map(CrewPosition::getPositionRole, MemberPositionSummaryDto::setRoleSummary);

                    mapper.map(CrewPosition::getPositionNote, MemberPositionSummaryDto::setPositionNote);

                    mapper.map(CrewPosition::getFilledAt, MemberPositionSummaryDto::setFilledAt);

                    mapper.map(CrewPosition::getCreatedAt, MemberPositionSummaryDto::setCreatedAt);
                });

//REFERENCE DATA
        // Gameplay Category ---->>>> Response Dto
        modelMapper.createTypeMap(GameplayCategory.class, GameplayCategoryDto.class)
                .addMappings(mapper -> {

                    //entity categoryId to dto gameplayCategoryId
                    mapper.map(GameplayCategory::getCategoryId, GameplayCategoryDto::setGameplayCategoryId);

                    //entity categoryName to dto gameplayCategoryName
                    mapper.map(GameplayCategory::getCategoryName, GameplayCategoryDto::setGameplayCategoryName);
                });

        //Gameplay Subcategory --->>> Response Dto
        modelMapper.createTypeMap(GameplaySubcategory.class, GameplaySubcategoryDto.class)
                .addMappings(mapper -> {

                    //skipping implicit mappings with matching field names
                    mapper.skip(GameplaySubcategoryDto::setSubcategoryId);
                    mapper.skip(GameplaySubcategoryDto::setSubcategoryName);

                    mapper.map(GameplaySubcategory::getGameplayCategory, GameplaySubcategoryDto::setGameplayCategoryId);

                    //subcategory entity gameplayCategory to dto gameplayCategoryName
                    mapper.map(GameplaySubcategory::getGameplayCategory, GameplaySubcategoryDto::setGameplayCategoryName);
                });


        return modelMapper;
    }


}
