package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationUnreadCountDto {

    public NotificationUnreadCountDto(Integer count) {
        this.count = count;
    }

    private Integer count;
}
