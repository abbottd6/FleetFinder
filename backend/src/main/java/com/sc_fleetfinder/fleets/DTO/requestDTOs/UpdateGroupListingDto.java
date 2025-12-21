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
public class UpdateGroupListingDto {

    //updating group listing will require having a groupId, creating a group listing will not pass a group id
    //updating a group listing will have a new update timestamp
    @NotNull(message = "Update group listing request DTO field 'groupId' cannot be null.")
    private Long groupId;

    @NotNull(message = "Update group listing request DTO field 'serverId' cannot be null")
    private Integer serverId;

    @NotNull(message = "Update group listing request DTO field 'environmentId' cannot be null")
    private Integer environmentId;

    @NotNull(message = "Update group listing request DTO field 'experienceId' cannot be null")
    private Integer experienceId;

    @NotBlank(message = "Update group listing request DTO field 'listingTitle' cannot be null")
    @Size(min = 2, max = 65, message = "Update group listing request DTO field 'listingTitle' must be " +
            "between 2 and 65 characters.")
    private String listingTitle;

    @Nullable
    private Integer playStyleId;

    @NotNull(message = "Update group listing request DTO field 'legalityId' cannot be null")
    private Integer legalityId;

    @NotNull(message = "Update group listing request DTO field 'groupStatusId' cannot be null")
    private Integer groupStatusId;

    @Nullable
    private String eventDate;

    @Nullable
    private String eventTime;

    @Nullable
    private String eventTimeZone;

    @NotNull(message = "Update group listing request DTO field 'categoryId' cannot be null")
    private Integer categoryId;

    @Nullable
    private Integer subcategoryId;

    @NotNull(message = "Update group listing request DTO field 'pvpStatusId' cannot be null")
    private Integer pvpStatusId;

    @NotNull(message = "Update group listing request DTO field 'systemId' cannot be null")
    private Integer systemId;

    @Nullable
    private Integer planetId;

    @NotBlank(message = "Update group listing request DTO field 'listingDescription' cxannot be blank")
    @Size(max = 500, min = 15, message = "Update group listing request DTO field 'listingDescription must be " +
            "between 15 and 500 characters.")
    private String listingDescription;

    @Min(value = 2, message = "Update group listing request DTO field 'desiredPartySize' cannot be less than 2.")
    @Max(value = 1000, message = "Update group listing request DTO field 'desiredPartySize' cannot exceed 1000.")
    @NotNull(message = "Update group listing request DTO field 'desiredPartySize' cannot be null.")
    private Integer desiredPartySize;

    @Min(value = 1, message = "Update group listing request DTO field 'currentPartySize' cannot be less than 1.")
    @Max(value = 1000, message = "Update group listing request DTO field 'currentPartySize' cannot exceed 1000.")
    @NotNull(message = "Update group listing request DTO field 'currentPartySize' cannot be null.")
    private Integer currentPartySize;

    @Size(max = 255, message = "Update group listing request DTO field 'availableRoles' cannot exceed 255 characters.")
    @Nullable
    private String availableRoles;

    @NotNull(message = "Update group listing request DTO field 'commsOption' cannot be null.")
    private String commsOption;

    @Size(max = 50, message = "Update group listing request DTO field 'listingCommsService' cannot exceed 50 chars.")
    @Nullable
    private String commsService;

}
