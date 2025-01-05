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

    @NotNull(message = "GroupListingDto groupId cannot be null")
    private Long groupId;

    @NotNull(message = "GroupListingDto userName cannot be null")
    private String userName;

    @NotNull(message = "GroupListingDto server cannot be null")
    private String server;

    @NotNull(message = "GroupListingDto environment cannot be null")
    private String environment;

    @NotNull(message = "GroupListingDto experience cannot be null")
    private String experience;

    @NotBlank(message = "GroupListingDto title cannot be empty")
    @Size(min = 2, max = 65, message = "Listing title must be between 2 and 65 characters")
    private String listingTitle;

    private String playStyle;

    @NotNull(message = "GroupListingDto legality cannot be null")
    private String legality;

    @NotNull(message = "GroupListingDto groupStatus cannot be null")
    private String groupStatus;

    @DateTimeFormat(pattern = "MM/dd/yy HH:mm")
    private LocalDateTime eventSchedule;

    @NotNull(message = "GroupListingDto category cannot be null")
    private String category;

    private String subcategory;

    @NotNull(message = "GroupListingDto pvpStatus cannot be null")
    private String pvpStatus;

    private String system;

    private String planetMoonSystem;

    @NotNull(message = "GroupListingDto listingDescription cannot be null")
    @Size(min = 0, max = 500, message = "Listing description cannot be longer than 400 characters")
    private String listingDescription;

    @Min(value = 2, message = "Desired party size must be at least 2.")
    @Max(value = 1000, message = "Desired party size cannot exceed 1,000.")
    private int desiredPartySize;

    private int currentPartySize;

    private String availableRoles;

    @NotNull(message = "GroupListingDto commsOption cannot be null")
    private String commsOption;

    private String commsService;

    @NotNull(message = "GroupListingDto creationTimestamp cannot be null")
    @DateTimeFormat(pattern = "MM/dd/yy HH:mm")
    private LocalDateTime creationTimestamp;

    @DateTimeFormat(pattern = "MM/dd/yy HH:mm")
    private LocalDateTime lastUpdated;
}
