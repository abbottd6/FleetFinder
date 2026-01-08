package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class ListingTemplateResponseDto {

    @NotNull(message = "ListingTemplateResponseDto field 'groupId' cannot be null")
    private Long templateId;

    @NotNull(message = "ListingTemplateResponseDto field 'userId' cannot be null.")
    private Long userId;

    private String server;
    private Integer serverId;
    private String environment;
    private Integer environmentId;
    private String experience;
    private Integer experienceId;
    private String listingTitle;
    private String playStyle;
    private Integer styleId;
    private String legality;
    private Integer legalityId;
    private String groupStatus;
    private Integer groupStatusId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant eventSchedule;
    private String category;
    private Integer categoryId;
    private String subcategory;
    private Integer subcategoryId;
    private String pvpStatus;
    private Integer pvpStatusId;
    private String system;
    private Integer systemId;
    private String planetMoonSystem;
    private Integer planetId;

    @Size(max = 500, message = "ListingTemplateResponseDto field 'listingDescription' cannot exceed 400 characters")
    private String listingDescription;

    @Max(value = 100, message = "ListingTemplateResponseDto field 'desiredPartySize' cannot exceed 1,000.")
    private Integer desiredPartySize;

    @Max(value = 100, message = "ListingTemplateResponseDto field 'currentPartySize' cannot exceed 1,000.")
    private Integer currentPartySize;
    private String availableRoles;
    private String commsOption;
    private String commsService;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant creationTimestamp;
}

