package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class GroupListingResponseDto {

    @NotNull(message = "GroupListingResponseDto field 'groupId' cannot be null")
    private Long groupId;

    @NotNull(message="GroupListingResponseDto field 'userId' cannot be null.")
    private Long userId;

    @NotNull(message = "GroupListingResponseDto field 'userName' cannot be null")
    private String userName;

    @NotNull(message = "GroupListingResponseDto field 'server' cannot be null")
    private String server;

    @NotNull(message = "GroupListingResponseDto field 'serverId' cannot be null")
    private Integer serverId;

    @NotNull(message = "GroupListingResponseDto field 'environment' cannot be null")
    private String environment;

    @NotNull(message = "GroupListingResponseDto field 'environmentId' cannot be null")
    private Integer environmentId;

    @NotNull(message = "GroupListingResponseDto field 'experience' cannot be null")
    private String experience;

    @NotNull(message = "GroupListingResponseDto field 'experienceId' cannot be null")
    private Integer experienceId;

    @NotBlank(message = "GroupListingResponseDto field 'listingTitle' cannot be empty")
    @Size(min = 2, max = 65, message = "GroupListingResponseDto field 'listingTitle' must be between 2 and 65 characters")
    private String listingTitle;

    private String playStyle;

    private Integer styleId;

    @NotNull(message = "GroupListingResponseDto field 'legality' cannot be null")
    private String legality;

    @NotNull(message = "GroupListingResponseDto field 'legalityId' cannot be null")
    private Integer legalityId;

    @NotNull(message = "GroupListingResponseDto field 'groupStatus' cannot be null")
    private String groupStatus;

    @NotNull(message = "GroupListingResponseDto field 'groupStatusId' cannot be null")
    private Integer groupStatusId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant eventSchedule;

    @NotNull(message = "GroupListingResponseDto field 'category' cannot be null")
    private String category;

    @NotNull(message = "GroupListingResponseDto field 'categoryId' cannot be null")
    private Integer categoryId;

    private String subcategory;

    private Integer subcategoryId;

    @NotNull(message = "GroupListingResponseDto field 'pvpStatus' cannot be null")
    private String pvpStatus;

    @NotNull(message = "GroupListingResponseDto field 'pvpStatusId' cannot be null")
    private Integer pvpStatusId;

    private String system;

    private Integer systemId;

    private String planetMoonSystem;

    private Integer planetId;

    @NotNull(message = "GroupListingResponseDto field 'listingDescription' cannot be null")
    @Size(max = 500, message = "GroupListingResponseDto field 'listingDescription' cannot exceed 400 characters")
    private String listingDescription;

    @Min(value = 2, message = "GroupListingResponseDto field 'desiredPartySize' must be at least 2.")
    @Max(value = 1000, message = "GroupListingResponseDto field 'desiredPartySize' cannot exceed 1,000.")
    private Integer desiredPartySize;

    @Min(value = 1, message = "GroupListingResponseDto field 'currentPartySize' size must be at least 1.")
    @Max(value = 1000, message = "GroupListingResponseDto field 'currentPartySize' cannot exceed 1,000.")
    private Integer currentPartySize;

    private String availableRoles;

    @NotNull(message = "GroupListingResponseDto field 'commsOption' cannot be null")
    private String commsOption;

    private String commsService;

    @NotNull(message = "GroupListingResponseDto field 'creationTimestamp' cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant creationTimestamp;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdated;
}
