package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.Data;

import java.time.Instant;

@Data
public class GetNotificationDto {
    private Long notificationId;
    private NotificationType type;
    private String title;
    private String message;
    private Instant createdAt;
}
