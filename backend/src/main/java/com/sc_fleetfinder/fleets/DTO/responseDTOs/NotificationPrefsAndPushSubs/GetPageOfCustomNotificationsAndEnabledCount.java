package com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class GetPageOfCustomNotificationsAndEnabledCount {

    public GetPageOfCustomNotificationsAndEnabledCount(Page<GetCustomNotificationResponseDto> page,
                                                       Integer enabledCount) {
        this.userCustomNotes = page;
        this.enabledCount = enabledCount;
    }

    private Page<GetCustomNotificationResponseDto> userCustomNotes;
    private Integer enabledCount;
}
