package com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs;

import lombok.Data;

import java.time.Instant;

@Data
public class GetPushSubDto {

    private Long idPushSub;
    private Long userId;
    private String userLabel;
    private Boolean sysNotesEnabled;
    private Boolean groupNotesEnabled;
    private Boolean socialNotesEnabled;
    private Integer dailyFailureCount;
    private Instant createdAt;
}
