package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class CreateGroupListingDto {

    @NotNull(message = "Create group listing DTO field 'userId' cannot be null")
    private Long userId;

    @NotNull(message = "Create group listing DTO field 'serverId' cannot be null")
    private int serverId;

    @NotNull(message = "Create group listing DTO field 'environmentId' cannot be null")
    private int environmentId;

    @NotNull(message = "Create group listing DTO field 'experienceId' cannot be null")
    private int experienceId;

    @NotBlank(message = "Create group listing DTO field 'listingTitle' cannot be blank")
    @Size(min = 2, max = 65, message = "Create group listing DTO field 'listing title must be" +
            "between 2 and 65 characters.")
    private String listingTitle;

    private int playStyleId;

    @NotNull(message = "Create group listing DTO field 'legalityId' cannot be null")
    private int legalityId;

    @NotNull(message = "Create group listing DTO field 'groupStatusId' cannot be null")
    private int groupStatusId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant eventSchedule;

    @NotNull(message = "Create group listing DTO field 'categoryId' cannot be null")
    private int categoryId;

    @NotNull(message = "Create group listing DTO field 'subcategoryId' cannot be null")
    private int subcategoryId;

    @NotNull(message = "Create group listing DTO field 'pvpStatusId' cannot be null")
    private int pvpStatusId;

    @NotNull(message = "Create group listing DTO field 'systemId' cannot be null")
    private int systemId;

    private int planetId;

    @NotBlank(message = "Create group listing DTO field 'listingDescription' cannot be blank")
    @Size(max = 500, message = "Create group listing DTO field 'listingDescription' cannot exceed 500 characters")
    private String listingDescription;

    @Min(value = 2, message = "Create group listing DTO field 'desiredPartySize' cannot be less than 2")
    @Max(value = 1000, message = "Create group listing DTO field 'desiredPartySize' cannot exceed 1000")
    private int desiredPartySize;

    @Min(value = 1, message = "Create listing DTO field 'currentPartySize' cannot be less than 1")
    @Max(value = 1000, message = "Create listing DTO field 'desiredPartySize' cannot exceed 1000")
    private int currentPartySize;

    @Size(max = 255, message = "Create listing DTO field 'availableRoles' cannot exceed 255 characters")
    private String availableRoles;

    @NotNull(message = "Create listing DTO field 'commsOption' cannot be null")
    private String commsOption;

    @Size(max = 50, message = "Create listing DTO field 'commsService' cannot exceed 50 characters")
    private String commsService;
}
