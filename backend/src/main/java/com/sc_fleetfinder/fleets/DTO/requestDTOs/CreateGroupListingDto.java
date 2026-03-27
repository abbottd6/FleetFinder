package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateGroupListingDto {

    // ##TODO should not include a userId here, nor on the front end, it should be derived from token
    private Long userId;

    @NotNull(message = "Create group listing DTO field 'serverId' cannot be null")
    private Integer serverId;

    @NotNull(message = "Create group listing DTO field 'environmentId' cannot be null")
    private Integer environmentId;

    @NotNull(message = "Create group listing DTO field 'experienceId' cannot be null")
    private Integer experienceId;

    @NotBlank(message = "Create group listing DTO field 'listingTitle' cannot be blank")
    @Size(min = 2, max = 65, message = "Create group listing DTO field 'listing title' must be" +
            "between 2 and 65 characters.")
    private String listingTitle;

    private Integer playStyleId;

    @NotNull(message = "Create group listing DTO field 'legalityId' cannot be null")
    private Integer legalityId;

    @NotNull(message = "Create group listing DTO field 'groupStatusId' cannot be null")
    private Integer groupStatusId;

    private String eventDate;

    private String eventTime;

    private String eventTimeZone;

    @NotNull(message = "Create group listing DTO field 'categoryId' cannot be null")
    private Integer categoryId;

    private Integer subcategoryId;

    @NotNull(message = "Create group listing DTO field 'pvpStatusId' cannot be null")
    private Integer pvpStatusId;

    @NotNull(message = "Create group listing DTO field 'systemId' cannot be null")
    private Integer systemId;

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
    private String availableRoles;

    @NotNull(message = "Create listing DTO field 'commsOption' cannot be null")
    private String commsOption;

    @Size(max = 50, message = "Create listing DTO field 'listingCommsService' cannot exceed 50 characters")
    private String commsService;

    @NotNull(message = "CreateGroupListingDto field 'languageCode' cannot be null")
    private LanguageOptions languageCode;
}
