package com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs;

import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class GetCustomNotificationResponseDto {

    @NotNull(message = "GetCustomNotificationResponseDto field 'customNoteId' cannot be null.")
    private Long customNoteId;

    @NotNull(message = "GetCustomNotificationResponseDto field 'enabled' cannot be null.")
    private Boolean enabled;

    private String tagLabel;

    private Integer serverId;
    private String server;

    private Integer environmentId;
    private String environment;

    private Integer experienceId;
    private String experience;

    private Integer categoryId;
    private String category;

    private Integer subcategoryId;
    private String subcategory;

    private Integer systemId;
    private String system;

    private LanguageOptions languageCode;

    private Integer pvpStatusId;
    private String pvpStatus;

    private Integer legalityId;
    private String legality;

    private Integer groupStatusId;
    private String groupStatus;

    private String keywords;

    private Instant createdAt;
}
