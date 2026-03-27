package com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs;

import lombok.Data;

@Data
public class UpdateUserNotePrefDto {

    private String label;
    private Boolean value;
}
