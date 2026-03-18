package com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrEditCustomNotificationDto {

    @Size(max = 32, message = "CreateOrEditCustomNotificationDto field " +
            "'tagLabel' cannot exceed 32 characters.")
    private String tagLabel;

    private Integer serverId;

    private Integer environmentId;

    private Integer experienceId;

    private Integer categoryId;

    private Integer subcategoryId;

    private Integer systemId;

    private String languageCode;

    private Integer pvpStatusId;

    private Integer legalityId;

    private Integer groupStatusId;

    private String keywords;
}
