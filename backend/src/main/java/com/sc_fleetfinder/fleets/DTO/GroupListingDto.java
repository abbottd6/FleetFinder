package com.sc_fleetfinder.fleets.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class GroupListingDto {

    @NotNull(message = "GroupListingDto field 'groupId' cannot be null")
    private Long groupId;

    @NotNull(message = "GroupListingDto field 'userName' cannot be null")
    private String userName;

    @NotNull(message = "GroupListingDto field 'server' cannot be null")
    private String server;

    @NotNull(message = "GroupListingDto field 'environment' cannot be null")
    private String environment;

    @NotNull(message = "GroupListingDto field 'experience' cannot be null")
    private String experience;

    @NotBlank(message = "GroupListingDto field 'listingTitle' cannot be empty")
    @Size(min = 2, max = 65, message = "GroupListingDto field 'listingTitle' must be between 2 and 65 characters")
    private String listingTitle;

    private String playStyle;

    @NotNull(message = "GroupListingDto field 'legality' cannot be null")
    private String legality;

    @NotNull(message = "GroupListingDto field 'groupStatus' cannot be null")
    private String groupStatus;

    @DateTimeFormat(pattern = "MM/dd/yy HH:mm")
    private LocalDateTime eventSchedule;

    @NotNull(message = "GroupListingDto field 'category' cannot be null")
    private String category;

    private String subcategory;

    @NotNull(message = "GroupListingDto field 'pvpStatus' cannot be null")
    private String pvpStatus;

    @NotNull(message="GroupListingDto field 'system' cannot be null")
    private String system;

    private String planetMoonSystem;

    @NotNull(message = "GroupListingDto field 'listingDescription' cannot be null")
    @Size(min = 0, max = 500, message = "GroupListingDto field 'listingDescription' cannot exceed 400 characters")
    private String listingDescription;

    @Min(value = 2, message = "GroupListingDto field 'desiredPartySize' must be at least 2.")
    @Max(value = 1000, message = "GroupListingDto field 'desiredPartySize' cannot exceed 1,000.")
    private int desiredPartySize;

    @Min(value = 1, message = "GroupListingDto field 'currentPartySize' size must be at least 1.")
    @Max(value = 1000, message = "GroupListingDto field 'currentPartySize' cannot exceed 1,000.")
    private int currentPartySize;

    private String availableRoles;

    @NotNull(message = "GroupListingDto field 'commsOption' cannot be null")
    private String commsOption;

    private String commsService;

    @NotNull(message = "GroupListingDto field 'creationTimestamp' cannot be null")
    @DateTimeFormat(pattern = "MM/dd/yy HH:mm")
    private LocalDateTime creationTimestamp;

    @DateTimeFormat(pattern = "MM/dd/yy HH:mm")
    private LocalDateTime lastUpdated;
}
