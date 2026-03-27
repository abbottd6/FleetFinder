package com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs;

import lombok.Data;

@Data
public class UpdatePushSubRequestDto {

    private Long idPushSub;
    private String label;
    private Boolean value;
}
