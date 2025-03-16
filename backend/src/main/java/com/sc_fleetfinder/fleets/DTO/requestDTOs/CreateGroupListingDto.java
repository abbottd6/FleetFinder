package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
public class CreateGroupListingDto {

    @NotNull(message = "Create group listing DTO field 'userId' cannot be null")
    private Long userId;

    @NotNull(message = "Create group listing DTO field 'serverId' cannot be null")
    private Integer serverId;

    @NotNull(message = "Create group listing DTO field 'environmentId' cannot be null")
    private Integer environmentId;

    @NotNull(message = "Create group listing DTO field 'experienceId' cannot be null")
    private Integer experienceId;

    @NotBlank(message = "Create group listing DTO field 'listingTitle' cannot be blank")
    @Size(min = 2, max = 65, message = "Create group listing DTO field 'listing title must be" +
            "between 2 and 65 characters.")
    private String listingTitle;

    @Nullable
    private Integer playStyleId;

    @NotNull(message = "Create group listing DTO field 'legalityId' cannot be null")
    private Integer legalityId;

    @NotNull(message = "Create group listing DTO field 'groupStatusId' cannot be null")
    private Integer groupStatusId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Nullable
    private Instant eventSchedule;

    @NotNull(message = "Create group listing DTO field 'categoryId' cannot be null")
    private Integer categoryId;

    @Nullable
    private Integer subcategoryId;

    @NotNull(message = "Create group listing DTO field 'pvpStatusId' cannot be null")
    private Integer pvpStatusId;

    @NotNull(message = "Create group listing DTO field 'systemId' cannot be null")
    private Integer systemId;

    @Nullable
    private Integer planetId;

    @NotBlank(message = "Create group listing DTO field 'listingDescription' cannot be blank")
    @Size(max = 500, message = "Create group listing DTO field 'listingDescription' cannot exceed 500 characters")
    private String listingDescription;

    @Min(value = 2, message = "Create group listing DTO field 'desiredPartySize' cannot be less than 2")
    @Max(value = 1000, message = "Create group listing DTO field 'desiredPartySize' cannot exceed 1000")
    @NotNull(message = "Create group listing DTO field 'desiredPartySize' cannot be null")
    private Integer desiredPartySize;

    @Min(value = 1, message = "Create listing DTO field 'currentPartySize' cannot be less than 1")
    @Max(value = 1000, message = "Create listing DTO field 'desiredPartySize' cannot exceed 1000")
    @NotNull(message = "Create group listing DTO field 'currentPartySize' cannot be null")
    private Integer currentPartySize;

    @Size(max = 255, message = "Create listing DTO field 'availableRoles' cannot exceed 255 characters")
    @Nullable
    private String availableRoles;

    @NotNull(message = "Create listing DTO field 'commsOption' cannot be null")
    private String commsOption;

    @Size(max = 50, message = "Create listing DTO field 'commsService' cannot exceed 50 characters")
    @Nullable
    private String commsService;
}
