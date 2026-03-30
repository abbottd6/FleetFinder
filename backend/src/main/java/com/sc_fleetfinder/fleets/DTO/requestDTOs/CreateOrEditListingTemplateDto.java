package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrEditListingTemplateDto {

    // ##TODO should not include a userId here, nor on the front end, it should be derived from token
    private Long userId;

    private Integer serverId;

    private Integer environmentId;

    private Integer experienceId;

    @Size(min = 2, max = 128, message = "Create group listing DTO field 'listing title' must be" +
            "between 2 and 65 characters.")
    private String listingTitle;

    private Integer playStyleId;

    private Integer legalityId;

    private Integer groupStatusId;

    private String eventDate;

    private String eventTime;

    private String eventTimeZone;

    private Integer categoryId;

    private Integer subcategoryId;

    private Integer pvpStatusId;

    private Integer systemId;

    private Integer planetId;

    @Size(max = 500, message = "Create group listing DTO field 'listingDescription' cannot exceed 500 characters")
    private String listingDescription;

    @Max(value = 1000, message = "Create group listing DTO field 'desiredPartySize' cannot exceed 1000")
    private Integer desiredPartySize;

    @Max(value = 1000, message = "Create listing DTO field 'desiredPartySize' cannot exceed 1000")
    private Integer currentPartySize;

    @Size(max = 255, message = "Create listing DTO field 'availableRoles' cannot exceed 255 characters")
    private String availableRoles;

    private String commsOption;

    @Size(max = 50, message = "Create listing DTO field 'listingCommsService' cannot exceed 50 characters")
    private String commsService;

    private LanguageOptions languageCode;
}
