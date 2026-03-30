package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import com.sc_fleetfinder.fleets.utils.ParentEntityReference;
import lombok.Data;

import java.time.Instant;

@Data
public class GetNotificationDto {
    private Long notificationId;
    private NotificationType type;
    private ParentEntityReference parentEntity;
    private String entityNewStatus;
    private String title;
    private String message;
    private NotificationTargetMetadata targetMetadata;
    private Instant createdAt;
}
